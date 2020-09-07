package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.ConsType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class ConsComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation car;
   protected final Computation cdr;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ConsComputation
   (
      final Origin origin,
      final Computation car,
      final Computation cdr
   )
   {
      super
      (
         origin,
         new ConsType(origin, car.get_type(), cdr.get_type, "auto generated")
      );

      this.car = car;
      this.cdr = cdr;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_cons_computation(this);
   }

   public Computation get_car ()
   {
      return car;
   }

   public Computation get_cdr ()
   {
      return cdr;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Cons ");
      sb.append(car.toString());
      sb.append(" ");
      sb.append(car.toString());
      sb.append(") ");

      return sb.toString();
   }
}
