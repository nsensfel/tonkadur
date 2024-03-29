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
import tonkadur.fate.v1.error.InvalidTypeArityException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class Type extends DeclaredEntity
{
   public static final Type ANY;
   public static final Type BOOL;
   public static final Type FLOAT;
   public static final Type INT;
   public static final Type STRING;
   public static final Type TEXT;

   public static final Set<Type> NUMBER_TYPES;
   public static final Set<Type> ALL_TYPES;
   public static final Set<Type> COMPARABLE_TYPES;
   public static final Set<Type> COLLECTION_TYPES;

   static
   {
      final Origin base;

      base = Origin.BASE_LANGUAGE;

      /*
       * Use of a space necessary to avoid conflicting with a user created type.
       */
      ANY = new Type(base, null, "(undetermined type)");

      BOOL = new Type(base, null, "bool");
      FLOAT = new Type(base, null, "float");
      INT = new Type(base, null, "int");
      TEXT = new Type(base, null, "text");
      STRING = new Type(base, null, "string");

      ALL_TYPES = new HashSet<Type>();
      ALL_TYPES.add(ANY);
      ALL_TYPES.add(BOOL);
      ALL_TYPES.add(ConsType.ARCHETYPE);
      ALL_TYPES.add(DictionaryType.ARCHETYPE);
      ALL_TYPES.add(FLOAT);
      ALL_TYPES.add(INT);
      ALL_TYPES.add(LambdaType.ARCHETYPE);
      ALL_TYPES.add(CollectionType.SET_ARCHETYPE);
      ALL_TYPES.add(CollectionType.LIST_ARCHETYPE);
      ALL_TYPES.add(PointerType.ARCHETYPE);
      ALL_TYPES.add(TEXT);
      ALL_TYPES.add(SequenceType.ARCHETYPE);
      ALL_TYPES.add(STRING);


      NUMBER_TYPES = new HashSet<Type>();
      NUMBER_TYPES.add(FLOAT);
      NUMBER_TYPES.add(INT);

      COMPARABLE_TYPES = new HashSet<Type>();

      COMPARABLE_TYPES.add(FLOAT);
      COMPARABLE_TYPES.add(INT);
      COMPARABLE_TYPES.add(SequenceType.ARCHETYPE);
      COMPARABLE_TYPES.add(LambdaType.ARCHETYPE);
      COMPARABLE_TYPES.add(STRING);
      COMPARABLE_TYPES.add(BOOL);
      COMPARABLE_TYPES.add(PointerType.ARCHETYPE);

      COLLECTION_TYPES = new HashSet<Type>();

      COLLECTION_TYPES.add(CollectionType.SET_ARCHETYPE);
      COLLECTION_TYPES.add(CollectionType.LIST_ARCHETYPE);
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

   public static List<Type> generate_default_parameters (final int i)
   {
      return Collections.nCopies(i, Type.ANY);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type true_type;
   protected final Type parent;
   protected final List<Type> parameters;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Accessors ************************************************************/
   public Type get_base_type ()
   {
      return true_type;
   }

   public Type get_act_as_type ()
   {
      return get_base_type();
   }

   public boolean is_base_type ()
   {
      return (parent == null);
   }

   public List<Type> get_parameters ()
   {
      return parameters;
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
      if (t instanceof FutureType)
      {
         return can_be_used_as(((FutureType) t).get_resolved_type());
      }
      else if (!get_act_as_type().equals(t.get_act_as_type()))
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
   public Type generate_alias (final Origin origin, final String name)
   {
      return new Type(origin, this, name);
   }

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

      for (final Type t: parameters)
      {
         sb.append("<");
         sb.append(t.toString());
         sb.append(">");
      }

      return sb.toString();
   }

   public Type generate_variant
   (
      final Origin origin,
      final List<Type> parameters
   )
   throws Throwable
   {
      if (this.parameters.size() != parameters.size())
      {
         ErrorManager.handle(new InvalidTypeArityException(origin, this));
      }

      return new Type(origin, get_base_type(), name, parameters);
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected Type
   (
      final Origin origin,
      final Type parent,
      final String name,
      final List<Type> parameters
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
      this.parameters = parameters;
   }

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
      this.parameters = generate_default_parameters(0);
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
