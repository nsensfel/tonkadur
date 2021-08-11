package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Clear;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;


public class ClearCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Clear.class;
   }

   public ClearCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Clear source;
      final ComputationCompiler address_compiler;
      final Address collection_address;

      source = (Clear) instruction;

      address_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection_address = address_compiler.get_address();

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_address
         )
      );

      address_compiler.release_registers(result);
   }
}
