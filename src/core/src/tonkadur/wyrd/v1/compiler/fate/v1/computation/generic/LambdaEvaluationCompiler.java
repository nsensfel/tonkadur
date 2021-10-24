package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.LambdaEvaluation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;


public class LambdaEvaluationCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return LambdaEvaluation.class;
   }

   public LambdaEvaluationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final LambdaEvaluation source;
      final ComputationCompiler lambda_cc;
      final List<Computation> parameters;
      final Register result;

      source = (LambdaEvaluation) computation;

      parameters = new ArrayList<Computation>();
      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      assimilate(lambda_cc);

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

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
         tonkadur.wyrd.v1.compiler.util.LambdaEvaluation.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            result_as_address,
            parameters
         )
      );
   }
}
