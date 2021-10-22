package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.PartialLambdaEvaluation;

import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class PartialLambdaEvaluationCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return PartialLambdaEvaluation.class;
   }

   public PartialLambdaEvaluationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final PartialLambdaEvaluation source;
      final ComputationCompiler parent_lambda_cc;
      final List<Computation> parameters;
      final Register result;
      final int max_number_of_arguments;

      source = (PartialLambdaEvaluation) computation;

      parameters = new ArrayList<Computation>();
      parent_lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(parent_lambda_cc);

      parent_lambda_cc.generate_address();

      assimilate(parent_lambda_cc);

      result = reserve(DictType.WILD);

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      max_number_of_arguments =
         (
            (LambdaType) source.get_lambda_function().get_type()
         ).get_signature().size();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation param:
            source.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         param.get_visited_by(cc);

         assimilate(cc);

         parameters.add(cc.get_computation());
      }

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.AddParametersToLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            parent_lambda_cc.get_address(),
            result_as_address,
            parameters,
            (max_number_of_arguments - parameters.size())
         )
      );
   }
}
