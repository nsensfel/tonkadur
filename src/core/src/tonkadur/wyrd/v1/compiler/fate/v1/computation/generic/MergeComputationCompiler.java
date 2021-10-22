package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.MergeComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class MergeComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return MergeComputation.class;
   }

   public MergeComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final MergeComputation source;
      final ComputationCompiler lambda_cc;
      final ComputationCompiler in_collection_a_cc, in_collection_b_cc;
      final Register result;

      source = (MergeComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      assimilate(lambda_cc);

      in_collection_a_cc = new ComputationCompiler(compiler);

      source.get_collection_in_a().get_visited_by(in_collection_a_cc);

      assimilate(in_collection_a_cc);

      in_collection_b_cc = new ComputationCompiler(compiler);

      source.get_collection_in_b().get_visited_by(in_collection_b_cc);

      assimilate(in_collection_b_cc);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.MergeLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            in_collection_a_cc.get_address(),
            in_collection_b_cc.get_address(),
            result_as_address,
            source.to_set()
         )
      );
   }
}
