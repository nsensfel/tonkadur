package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.PushElement;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class PushElementCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return PushElement.class;
   }

   public PushElementCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final PushElement source;
      final ComputationCompiler address_compiler, element_compiler;
      final Register collection_size, index;

      source = (PushElement) instruction;

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

      collection_size = compiler.registers().reserve(Type.INT, result);
      index = compiler.registers().reserve(Type.INT, result);

      result.add
      (
         new SetValue
         (
            collection_size.get_address(),
            new Size(address_compiler.get_address())
         )
      );

      result.add
      (
         new SetValue
         (
            index.get_address(),
            (
               source.is_from_left() ?
                  Constant.ZERO
                  : collection_size.get_value()
            )
         )
      );

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index.get_address(),
            element_compiler.get_computation(),
            collection_size.get_value(),
            address_compiler.get_address()
         )
      );

      address_compiler.release_registers(result);
      element_compiler.release_registers(result);

      compiler.registers().release(collection_size, result);
      compiler.registers().release(index, result);
   }
}
