package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class LambdaEvaluation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected LambdaEvaluation
   (
      final Origin origin,
      final Computation lambda_function,
      final List<Computation> parameters,
      final Type act_as
   )
   {
      super(origin, act_as);

      this.lambda_function = lambda_function;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static LambdaEvaluation build
   (
      final Origin origin,
      final Computation lambda_function,
      final List<Computation> parameters
   )
   throws ParsingError
   {
      RecurrentChecks.assert_lambda_matches_computations
      (
         lambda_function,
         parameters
      );

      return
         new LambdaEvaluation
         (
            origin,
            lambda_function,
            parameters,
            (((LambdaType) lambda_function.get_type()).get_return_type())
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_lambda_evaluation(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(LambdaEvaluation (");
      sb.append(lambda_function.toString());

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append("))");

      return sb.toString();
   }
}
