package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.PushElementComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class PushElementComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return PushElementComputation.class;
   }

   public PushElementComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final PushElementComputation source;
      final ComputationCompiler address_compiler, element_compiler;
      final Register result, collection_size, index;

      source = (PushElementComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      source.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         init_instructions.add(element_compiler.get_init());
      }

      collection_size = reserve(Type.INT);
      index = reserve(Type.INT);

      init_instructions.add
      (
         new SetValue
         (
            collection_size.get_address(),
            new Size(result_as_address)
         )
      );

      init_instructions.add
      (
         new SetValue
         (
            index.get_address(),
            (
               source.is_from_left() ?
               Constant.ZERO :
               collection_size.get_value()
            )
         )
      );

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index.get_address(),
            element_compiler.get_computation(),
            collection_size.get_value(),
            result_as_address
         )
      );

      element_compiler.release_registers(init_instructions);
   }
}
