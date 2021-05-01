package tonkadur.fate.v1.lang.type;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class ExtraType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public ExtraType
   (
      final Origin origin,
      final String name
   )
   {
      super(origin, null, name);
   }

   public ExtraType
   (
      final Origin origin,
      final ExtraType parent,
      final String name
   )
   {
      super(origin, parent, name);
   }

   /**** Accessors ************************************************************/

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof ExtraType)
      {
         final ExtraType e;

         e = (ExtraType) t;

         return get_base_type().get_name().equals(e.get_base_type().get_name());

      }

      return false;
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
      return Type.ANY;
   }

   @Override
   public Type get_act_as_type ()
   {
      return get_base_type();
   }

   /**** Misc. ****************************************************************/

   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new ExtraType(origin, this, name);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("ExtraType::");
      sb.append(name);

      return sb.toString();
   }
}
