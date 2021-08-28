package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.PopElement;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class PopElementCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return PopElement.class;
   }

   public PopElementCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final PopElement source;
      final ComputationCompiler address_compiler, element_compiler;

      source = (PopElement) instruction;

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      source.get_storage().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.PopElement.generate
         (
            compiler.registers(),
            compiler.assembler(),
            address_compiler.get_address(),
            element_compiler.get_address(),
            source.is_from_left()
         )
      );

      address_compiler.release_registers(result);
      element_compiler.release_registers(result);
   }
}
