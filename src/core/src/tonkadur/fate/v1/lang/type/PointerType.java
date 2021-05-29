package tonkadur.fate.v1.lang.type;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class PointerType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type referenced_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public PointerType
   (
      final Origin origin,
      final Type referenced_type,
      final String name
   )
   {
      super(origin, null, name);

      this.referenced_type = referenced_type;
   }

   /**** Accessors ************************************************************/
   public Type get_referenced_type ()
   {
      return referenced_type;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof PointerType)
      {
         final PointerType dt;

         dt = (PointerType) t;

         return referenced_type.can_be_used_as(dt.referenced_type);
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
      final Type resulting_referenced_type;
      final PointerType dt;

      if (!(de instanceof PointerType))
      {
         return Type.ANY;
      }

      dt = (PointerType) de;
      resulting_referenced_type =
         (Type) referenced_type.generate_comparable_to(dt.referenced_type);

      return new PointerType(get_origin(), resulting_referenced_type, name);
   }

   @Override
   public Type get_act_as_type ()
   {
      return Type.PTR;
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new PointerType(origin, referenced_type, name);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Pointer to ");
      sb.append(referenced_type.toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }
}
