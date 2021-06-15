package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.ExtraComputation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class ExtraComputationInstance extends GenericComputation
{
   protected final ExtraComputation computation;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ExtraComputationInstance
   (
      final Origin origin,
      final ExtraComputation computation,
      final List<Computation> parameters
   )
   {
      super(origin, computation.get_returned_type(), computation.get_name());

      this.computation = computation;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ExtraComputationInstance build
   (
      final Origin origin,
      final ExtraComputation computation,
      final List<Computation> parameters
   )
   throws ParsingError
   {
      RecurrentChecks.assert_computations_matches_signature
      (
         origin,
         parameters,
         computation.get_signature()
      );

      return new ExtraComputationInstance(origin, computation, parameters);
   }

   /**** Accessors ************************************************************/
   public ExtraComputation get_computation_type ()
   {
      return computation;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_extra_computation(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(");
      sb.append(computation.get_name());

      for (final Computation p: parameters)
      {
         sb.append(" ");
         sb.append(p.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
