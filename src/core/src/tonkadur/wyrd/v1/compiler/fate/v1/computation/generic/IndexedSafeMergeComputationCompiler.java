package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.IndexedSafeMergeComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class IndexedSafeMergeComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return IndexedSafeMergeComputation.class;
   }

   public IndexedSafeMergeComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final IndexedSafeMergeComputation source;
      final ComputationCompiler lambda_cc, default_a_cc, default_b_cc;
      final ComputationCompiler in_collection_a_cc, in_collection_b_cc;
      final Register result;

      source = (IndexedSafeMergeComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      assimilate(lambda_cc);

      default_a_cc = new ComputationCompiler(compiler);

      source.get_default_a().get_visited_by(default_a_cc);

      default_a_cc.generate_address();

      assimilate(default_a_cc);

      default_b_cc = new ComputationCompiler(compiler);

      source.get_default_b().get_visited_by(default_b_cc);

      default_b_cc.generate_address();

      assimilate(default_b_cc);

      in_collection_a_cc = new ComputationCompiler(compiler);

      source.get_collection_in_a().get_visited_by(in_collection_a_cc);

      assimilate(in_collection_a_cc);

      in_collection_b_cc = new ComputationCompiler(compiler);

      source.get_collection_in_b().get_visited_by(in_collection_b_cc);

      assimilate(in_collection_b_cc);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.IndexedMergeLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            default_a_cc.get_computation(),
            in_collection_a_cc.get_address(),
            default_b_cc.get_computation(),
            in_collection_b_cc.get_address(),
            result_as_address,
            source.to_set()
         )
      );

   }
}
