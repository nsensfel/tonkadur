package tonkadur.fate.v1.lang.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.functional.Merge;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class LambdaEvaluation extends GenericComputation
{
   protected static final LambdaEvaluation ARCHETYPE;

   static
   {
      final List<String> aliases;

      ARCHETYPE =
         new LambdaEvaluation
         (
            Origin.BASE_LANGUAGE,
            null,
            null,
            Type.BOOL
         );

      aliases = new ArrayList<String>();

      aliases.add("eval");
      aliases.add("evaluate");

      try
      {
         ARCHETYPE.register(aliases, null);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }
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
   protected LambdaEvaluation
   (
      final Origin origin,
      final Computation lambda_function,
      final List<Computation> parameters,
      final Type act_as
   )
   {
      super(origin, act_as, "eval");

      this.lambda_function = lambda_function;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   @Override
   public GenericComputation build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object _registered_parameter
   )
   throws Throwable
   {
      final Computation lambda_function;
      final List<Type> lambda_signature;

      if (call_parameters.size() < 1)
      {
         // TODO: Error.
      }

      lambda_function = call_parameters.get(0);

      lambda_function.expect_non_string();

      RecurrentChecks.assert_is_a_lambda_function(lambda_function);

      lambda_signature =
         ((LambdaType) lambda_function.get_type()).get_signature();

      call_parameters.remove(0);

      RecurrentChecks.propagate_expected_types
      (
         call_parameters,
         lambda_signature
      );

      RecurrentChecks.assert_computations_matches_signature
      (
         origin,
         call_parameters,
         lambda_signature
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
