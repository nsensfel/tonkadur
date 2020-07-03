package tonkadur.fate.v1.lang;

import tonkadur.parser.Origin;

public class Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Origin origin;
   protected final String name;
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
      if (parent == null)
      {
         true_type = this;
      }
      else
      {
         true_type = parent.true_type;
      }

      this.origin = origin;
      this.parent = parent;
      this.name = name;
   }

   /**** Accessors ************************************************************/
   public String get_name ()
   {
      return name;
   }

   public Origin get_origin ()
   {
      return origin;
   }

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
   public boolean equals (final Object o)
   {
      if (o instanceof Type)
      {
         final Type t;

         t = (Type) o;

         return name.equals(t.name);
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return name.hashCode();
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
