package tonkadur.fate.v1.lang.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class Type extends DeclaredEntity
{
   public static final Type ANY;
   public static final Type BOOLEAN;
   public static final Type DICT;
   public static final Type FLOAT;
   public static final Type INT;
   public static final Type LAMBDA;
   public static final Type LIST;
   public static final Type REF;
   public static final Type RICH_TEXT;
   public static final Type SET;
   public static final Type STRING;

   public static final Set<Type> NUMBER_TYPES;
   public static final Set<Type> ALL_TYPES;
   public static final Set<Type> SIMPLE_BASE_TYPES;
   public static final Set<Type> COLLECTION_TYPES;

   static
   {
      final Origin base;

      base = Origin.BASE_LANGUAGE;

      /*
       * Use of a space necessary to avoid conflicting with a user created type.
       */
      ANY = new Type(base, null, "undetermined type");

      BOOLEAN = new Type(base, null, "boolean");
      DICT = new Type(base, null, "dict");
      FLOAT = new Type(base, null, "float");
      INT = new Type(base, null, "int");
      LAMBDA = new Type(base, null, "lambda");
      LIST = new Type(base, null, "list");
      REF = new Type(base, null, "ref");
      RICH_TEXT = new Type(base, null, "text");
      SET = new Type(base, null, "set");
      STRING = new Type(base, null, "string");

      ALL_TYPES = new HashSet<Type>();
      ALL_TYPES.add(ANY);
      ALL_TYPES.add(BOOLEAN);
      ALL_TYPES.add(DICT);
      ALL_TYPES.add(FLOAT);
      ALL_TYPES.add(INT);
      ALL_TYPES.add(LAMBDA);
      ALL_TYPES.add(LIST);
      ALL_TYPES.add(REF);
      ALL_TYPES.add(RICH_TEXT);
      ALL_TYPES.add(SET);
      ALL_TYPES.add(STRING);


      NUMBER_TYPES = new HashSet<Type>();
      NUMBER_TYPES.add(FLOAT);
      NUMBER_TYPES.add(INT);

      SIMPLE_BASE_TYPES = new HashSet<Type>();

      SIMPLE_BASE_TYPES.add(FLOAT);
      SIMPLE_BASE_TYPES.add(INT);
      SIMPLE_BASE_TYPES.add(LAMBDA);
      SIMPLE_BASE_TYPES.add(STRING);
      SIMPLE_BASE_TYPES.add(BOOLEAN);
      SIMPLE_BASE_TYPES.add(REF);

      COLLECTION_TYPES = new HashSet<Type>();

      COLLECTION_TYPES.add(SET);
      COLLECTION_TYPES.add(LIST);
   }

   public static Type value_on_missing ()
   {
      return ANY;
   }

   @Override
   public /* static */ String get_type_name ()
   {
      return "Type";
   }


   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type true_type;
   protected final Type parent;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public static Type build
   (
      final Origin origin,
      final Type parent,
      final String name
   )
   throws InvalidTypeException
   {
      if (!SIMPLE_BASE_TYPES.contains(parent.get_act_as_type()))
      {
         ErrorManager.handle
         (
            new InvalidTypeException(origin, parent, SIMPLE_BASE_TYPES)
         );
      }

      return new Type(origin, parent, name);
   }

   /**** Accessors ************************************************************/
   public Type get_base_type ()
   {
      return true_type;
   }

   public Type get_act_as_type ()
   {
      return true_type;
   }

   public boolean is_base_type ()
   {
      return (parent == null);
   }

   public Type try_merging_with (final Type t)
   {
      if (t.get_base_type() != get_base_type())
      {
         return null;
      }

      if (t.is_base_type())
      {
         return this;
      }

      if (is_base_type())
      {
         return t;
      }

      return null;
   }

   /**** Compatibility ********************************************************/
   public boolean can_be_used_as (final Type t)
   {
      if (!get_act_as_type().equals(t.get_act_as_type()))
      {
         return false;
      }

      return this_or_parent_equals(t);
   }

   /*
    * This is for the very special case where a type is used despite not being
    * even a sub-type of the expected one. Using this rather expensive function,
    * the most restrictive shared type will be returned. If no such type exists,
    * the ANY time is returned.
    */
   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      final Iterator<Type> it0, it1;
      Type result, candidate;

      if (!(de instanceof Type))
      {
         return ANY;
      }

      it0 = ((Type) de).compute_full_type_chain().iterator();
      it1 = compute_full_type_chain().iterator();

      result = Type.ANY;

      while (it0.hasNext() && it1.hasNext())
      {
         candidate = it0.next();

         if (!candidate.name.equals(it1.next().name))
         {
            break;
         }

         result = candidate;
      }

      return new Type(origin, result, name);
   }


   /**** Misc. ****************************************************************/
   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      if (de instanceof Type)
      {
         final Type t;

         t = (Type) de;

         /*
          * If the previous type cannot be used when the new one will do, the
          * new declaration cannot safely stand.
          */
         return !can_be_used_as(t);
      }

      return true;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (parent != null)
      {
         sb.append(parent.toString());
         sb.append("::");
      }

      sb.append(name);

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected Type
   (
      final Origin origin,
      final Type parent,
      final String name
   )
   {
      super(origin, name);

      if (parent == null)
      {
         true_type = this;
      }
      else
      {
         true_type = parent.true_type;
      }

      this.parent = parent;
   }

   protected boolean this_or_parent_equals (final Type t)
   {
      if (equals(t))
      {
         return true;
      }

      if (parent == null)
      {
         return false;
      }

      return parent.this_or_parent_equals(t);
   }

   protected List<Type> compute_full_type_chain ()
   {
      final List<Type> result;
      Type t;

      result = new ArrayList<Type>();

      t = this;

      while (t != null)
      {
         result.add(t);

         t = t.parent;
      }

      Collections.reverse(result);

      return result;
   }
}
