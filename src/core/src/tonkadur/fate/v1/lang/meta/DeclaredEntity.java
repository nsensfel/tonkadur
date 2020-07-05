package tonkadur.fate.v1.lang.meta;

import tonkadur.parser.Origin;

public abstract class DeclaredEntity
{
   /***************************************************************************/
   /**** STATIC ***************************************************************/
   /***************************************************************************/
   /* I wish it could be static, but it can't, because of Java limitations. */
   public /* static */ String get_type_name ()
   {
      return "Declared Entity";
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Origin origin;
   protected final String name;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   protected DeclaredEntity
   (
      final Origin origin,
      final String name
   )
   {
      this.origin = origin;
      this.name = name;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Accessors ************************************************************/
   public String get_name ()
   {
      return name;
   }

   public Origin get_origin ()
   {
      return origin;
   }

   /**** Compatibility ********************************************************/
   public boolean conflicts_with_declaration (final DeclaredEntity de)
   {
      return !equals(de);
   }

   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      return !equals(de);
   }

   public <ThisType extends DeclaredEntity>
   ThisType generate_comparable_to (final ThisType de)
   {
      return null;
   }

   /**** Misc. ****************************************************************/
   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof DeclaredEntity)
      {
         final DeclaredEntity de;

         de = (DeclaredEntity) o;

         return toString().equals(de.toString());
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return toString().hashCode();
   }

   @Override
   public String toString ()
   {
      return name;
   }
}
