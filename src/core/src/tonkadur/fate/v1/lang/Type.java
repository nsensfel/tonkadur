package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class Type extends DeclaredEntity
{
   public static final Type ANY;
   public static final Type BOOLEAN;
   public static final Type DICT;
   public static final Type FLOAT;
   public static final Type INT;
   public static final Type LIST;
   public static final Type SET;
   public static final Type STRING;

   public static final Set<Type> NUMBER_TYPES;
   public static final Set<Type> SET_COMPATIBLE_TYPES;

   static
   {
      final Origin base;

      base = new Origin(new Context(""), Location.BASE_LANGUAGE);

      /*
       * Use of a space necessary to avoid conflicting with a user created type.
       */
      ANY = new Type(base, null, "undetermined type");

      BOOLEAN = new Type(base, null, "boolean");
      DICT = new Type(base, null, "dict");
      FLOAT = new Type(base, null, "float");
      INT = new Type(base, null, "int");
      LIST = new Type(base, null, "list");
      SET = new Type(base, null, "set");
      STRING = new Type(base, null, "string");

      NUMBER_TYPES = new HashSet<Type>();
      NUMBER_TYPES.add(FLOAT);
      NUMBER_TYPES.add(INT);

      SET_COMPATIBLE_TYPES = new HashSet<Type>();

      SET_COMPATIBLE_TYPES.add(FLOAT);
      SET_COMPATIBLE_TYPES.add(INT);
      SET_COMPATIBLE_TYPES.add(STRING);
      SET_COMPATIBLE_TYPES.add(BOOLEAN);
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
   public Type
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

   /**** Accessors ************************************************************/
   public Type get_true_type ()
   {
      return true_type;
   }

   public boolean is_base_type ()
   {
      return (parent == null);
   }

   /**** Compatibility ********************************************************/
   public boolean can_be_used_as (final Type t)
   {
      if (!true_type.equals(t.true_type))
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
