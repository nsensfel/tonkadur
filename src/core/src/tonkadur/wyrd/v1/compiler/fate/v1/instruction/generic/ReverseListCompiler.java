package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.ReverseList;

import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class ReverseListCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return ReverseList.class;
   }

   public ReverseListCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final ReverseList source;
      final ComputationCompiler address_compiler;
      final Address collection_address;

      source = (ReverseList) instruction;

      address_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection_address = address_compiler.get_address();

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.ReverseList.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new Size(collection_address),
            collection_address
         )
      );

      address_compiler.release_registers(result);
   }
}
