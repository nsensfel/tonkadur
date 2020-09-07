package tonkadur.fate.v1.lang.type;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class ConsType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type car;
   protected final Type cdr;

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
      super(origin, null, name);

      this.car = car;
      this.cdr = cdr;
   }

   /**** Accessors ************************************************************/
   public Type get_car_type ()
   {
      return car;
   }

   public Type get_cdr_type ()
   {
      return cdr;
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
            car.can_be_used_as(dt.car)
            && cdr.can_be_used_as(dt.cdr);
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

      resulting_car = (Type) car.generate_comparable_to(dt.car);
      resulting_cdr = (Type) cdr.generate_comparable_to(dt.cdr);

      return new ConsType(get_origin(), resulting_car, resulting_cdr, name);
   }

   @Override
   public Type get_act_as_type ()
   {
      return Type.CONS;
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new ConsType(origin, car, cdr, name);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Cons ");
      sb.append(car.toString());
      sb.append(" ");
      sb.append(cdr.toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }
}
