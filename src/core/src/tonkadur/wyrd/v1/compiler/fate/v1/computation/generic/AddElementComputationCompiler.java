package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.AddElementComputation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class AddElementComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return AddElementComputation.class;
   }

   public AddElementComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final AddElementComputation source;
      final ComputationCompiler address_compiler, element_compiler;
      final Register result;

      source = (AddElementComputation) computation;

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
      assimilate(element_compiler);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.AddElement.generate
         (
            compiler.registers(),
            compiler.assembler(),
            element_compiler.get_computation(),
            result_as_address,
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
            ).is_set()
         )
      );
   }
}
