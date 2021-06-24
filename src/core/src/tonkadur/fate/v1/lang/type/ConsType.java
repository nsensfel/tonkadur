package tonkadur.fate.v1.lang.type;

import java.util.Arrays;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class ConsType extends Type
{
   public static final ConsType ARCHETYPE;

   static
   {

      ARCHETYPE =
         new ConsType
         (
            Origin.BASE_LANGUAGE,
            Type.ANY,
            Type.ANY,
            "cons"
         );
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public ConsType
   (
      final Origin origin,
      final Type car,
      final Type cdr,
      final String name
   )
   {
      super(origin, null, name, Arrays.asList(new Type[]{car, cdr}));
   }

   /**** Accessors ************************************************************/
   public Type get_car_type ()
   {
      return parameters.get(0);
   }

   public Type get_cdr_type ()
   {
      return parameters.get(1);
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof ConsType)
      {
         final ConsType dt;

         dt = (ConsType) t;

         return
            get_car_type().can_be_used_as(dt.get_car_type())
            && get_cdr_type().can_be_used_as(dt.get_cdr_type());
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
      final Type resulting_car, resulting_cdr;
      final ConsType dt;

      if (!(de instanceof ConsType))
      {
         return Type.ANY;
      }

      dt = (ConsType) de;

      resulting_car =
         (Type) get_car_type().generate_comparable_to(dt.get_car_type());

      resulting_cdr =
         (Type) get_cdr_type().generate_comparable_to(dt.get_cdr_type());

      return new ConsType(get_origin(), resulting_car, resulting_cdr, name);
   }

   @Override
   public Type get_act_as_type ()
   {
      return ARCHETYPE;
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new ConsType(origin, get_car_type(), get_cdr_type(), name);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Cons ");
      sb.append(get_car_type().toString());
      sb.append(" ");
      sb.append(get_cdr_type().toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }
}
