package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.ArrayList;
import java.util.List;

import tonkadur.fate.v1.lang.computation.generic.SortComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class SortComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return SortComputation.class;
   }

   public SortComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final SortComputation source;
      final ComputationCompiler lambda_cc, in_collection_cc;
      final Register result;

      source = (SortComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      assimilate(lambda_cc);

      in_collection_cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(in_collection_cc);

      if (in_collection_cc.has_init())
      {
         init_instructions.add(in_collection_cc.get_init());
      }

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.Sort.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            in_collection_cc.get_address(),
            result_as_address
         )
      );

      in_collection_cc.release_registers(init_instructions);
   }
}
