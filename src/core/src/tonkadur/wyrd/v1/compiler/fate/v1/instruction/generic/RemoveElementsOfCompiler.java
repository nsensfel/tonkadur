package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.RemoveElementsOf;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class RemoveElementsOfCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return RemoveElementsOf.class;
   }

   public RemoveElementsOfCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final RemoveElementsOf source;
      final ComputationCompiler collection_in_cc, collection_cc;

      source = (RemoveElementsOf) instruction;

      collection_cc = new ComputationCompiler(compiler);

      source.get_target_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection_in_cc = new ComputationCompiler(compiler);

      source.get_source_collection().get_visited_by(collection_in_cc);

      if (collection_in_cc.has_init())
      {
         result.add(collection_in_cc.get_init());
      }

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.RemoveElementsOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_in_cc.get_address(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_target_collection().get_type()
            ).is_set()
         )
      );

      collection_cc.release_registers(result);
      collection_in_cc.release_registers(result);
   }
}
