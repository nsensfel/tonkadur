package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.Filter;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class FilterCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Filter.class;
   }

   public FilterCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Filter source;
      final ComputationCompiler lambda_cc, collection_cc;

      source = (Filter) instruction;

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

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.FilterLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            collection_cc.get_address()
         )
      );

      lambda_cc.release_registers(result);
      collection_cc.release_registers(result);
   }
}
