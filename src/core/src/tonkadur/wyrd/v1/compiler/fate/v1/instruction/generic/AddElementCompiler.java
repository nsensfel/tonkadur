package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.AddElement;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class AddElementCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return AddElement.class;
   }

   public AddElementCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final AddElement source;
      final ComputationCompiler address_compiler, element_compiler;

      source = (AddElement) instruction;

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      source.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.AddElement.generate
         (
            compiler.registers(),
            compiler.assembler(),
            element_compiler.get_computation(),
            address_compiler.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
            ).is_set()
         )
      );

      element_compiler.release_registers(result);
      address_compiler.release_registers(result);
   }
}
