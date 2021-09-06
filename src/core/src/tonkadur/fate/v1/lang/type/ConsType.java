package tonkadur.fate.v1.lang.type;

import java.util.Arrays;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeArityException;

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

   @Override
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

      return new ConsType(origin, parameters.get(0), parameters.get(1), name);
   }

   /**** Accessors ************************************************************/
   public Type get_car_type ()
   {
      final Type result;

      result = parameters.get(0);

      if (result instanceof FutureType)
      {
         return ((FutureType) result).get_resolved_type();
      }

      return result;
   }

   public Type get_cdr_type ()
   {
      final Type result;

      result = parameters.get(1);

      if (result instanceof FutureType)
      {
         return ((FutureType) result).get_resolved_type();
      }

      return result;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof FutureType)
      {
         return can_be_used_as(((FutureType) t).get_resolved_type());
      }
      else if (t instanceof ConsType)
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
