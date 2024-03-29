package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.Sort;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class SortCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Sort.class;
   }

   public SortCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Sort source;
      final ComputationCompiler lambda_cc;
      final ComputationCompiler collection_cc;
      final Register sorted_result;

      source = (Sort) instruction;

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      sorted_result =
         compiler.registers().reserve
         (
            collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.Sort.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            collection_cc.get_address(),
            sorted_result.get_address()
         )
      );

      result.add
      (
         new SetValue(collection_cc.get_address(), sorted_result.get_value())
      );

      compiler.registers().release(sorted_result, result);

      collection_cc.release_registers(result);
   }
}
