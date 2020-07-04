package tonkadur.fate.v1.lang;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class Type extends DeclaredEntity
{
   protected static final Type ANY;

   static
   {
      ANY =
         new Type
         (
            new Origin(new Context(""), Location.BASE_LANGUAGE),
            null,
            /*
             * Use of a space necessary to avoid conflicting with a user created
             * type.
             */
            "undetermined type"
         );
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
}
