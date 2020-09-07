package tonkadur.fate.v1.lang.computation;

import java.util.Collections;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.Type;

public class CarCdr extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;
   protected final boolean is_car;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CarCdr
   (
      final Origin origin,
      final Computation parent,
      final boolean is_car,
      final Type type
   )
   {
      super(origin, type);

      this.parent = parent;
      this.is_car = is_car;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static CarCdr build
   (
      final Origin origin,
      final Computation parent,
      final boolean is_car
   )
   throws InvalidTypeException
   {
      Type current_type;

      current_type = parent.get_type();

      if (!(current_type instanceof ConsType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singletonList(Type.CONS)
            )
         );

         current_type = Type.ANY;
      }
      else if (is_car)
      {
         current_type = ((ConsType) current_type).get_car_type();
      }
      else
      {
         current_type = ((ConsType) current_type).get_cdr_type();
      }

      return new CarCdr(origin, parent, is_car, current_type);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_car_cdr(this);
   }

   public Computation get_parent ()
   {
      return parent;
   }

   public boolean is_car ()
   {
      return is_car;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (is_car)
      {
         sb.append("(car (");
      }
      else
      {
         sb.append("(cdr (");
      }

      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent);
      sb.append(")");

      return sb.toString();
   }
}
