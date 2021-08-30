package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Shuffle;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class ShuffleCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Shuffle.class;
   }

   public ShuffleCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Shuffle source;
      final ComputationCompiler address_compiler;
      final Address collection_address;

      source = (Shuffle) instruction;

      address_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection_address = address_compiler.get_address();

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.Shuffle.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_address
         )
      );

      address_compiler.release_registers(result);
   }
}
