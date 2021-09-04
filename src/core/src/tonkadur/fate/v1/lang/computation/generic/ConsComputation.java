package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.ConsType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class ConsComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("cons");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation car;
      final Computation cdr;

      if (call_parameters.size() != 2)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      car = call_parameters.get(0);
      cdr = call_parameters.get(1);

      car.expect_string();
      cdr.expect_string();

      return new ConsComputation(origin, car, cdr);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation car;
   protected final Computation cdr;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ConsComputation
   (
      final Origin origin,
      final Computation car,
      final Computation cdr
   )
   {
      super
      (
         origin,
         new ConsType(origin, car.get_type(), cdr.get_type(), "auto generated")
      );

      this.car = car;
      this.cdr = cdr;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
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
