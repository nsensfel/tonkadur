package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.functional.Merge;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class PartialLambdaEvaluation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("partial");
      aliases.add("partial_eval");
      aliases.add("partialeval");
      aliases.add("partialEval");
      aliases.add("partial_evaluate");
      aliases.add("partialevaluate");
      aliases.add("partialEvaluate");

      return aliases;
   }

   /**** Constructors *********************************************************/
   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation lambda_function;
      final List<Computation> parameters;
      final LambdaType initial_type;
      final List<Type> initial_signature;
      final List<Type> remaining_signature;

      if (call_parameters.size() < 1)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + " <(LAMBDA X (Y0...YN))> <Y0>...<YM, with M =< N>)"
            )
         );

         return null;
      }

      lambda_function = call_parameters.get(0);
      parameters = call_parameters.subList(1, call_parameters.size());

      RecurrentChecks.propagate_partial_expected_types_and_assert_is_lambda
      (
         lambda_function,
         new ArrayList<Type>(),
         parameters
      );

      initial_type = (LambdaType) lambda_function.get_type();
      initial_signature = initial_type.get_signature();
      remaining_signature =
         initial_signature.subList
         (
            (call_parameters.size() - 1),
            initial_signature.size()
         );

      return
         new PartialLambdaEvaluation
         (
            origin,
            lambda_function,
            parameters,
            new LambdaType
            (
               origin,
               initial_type.get_return_type(),
               ("(Partial from " + initial_type.get_name() + ")"),
               remaining_signature
            )
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PartialLambdaEvaluation
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

   /**** Accessors ************************************************************/
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

      sb.append("(PartialLambdaEvaluation (");
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
