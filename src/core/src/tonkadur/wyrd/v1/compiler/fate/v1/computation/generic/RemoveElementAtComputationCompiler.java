package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.RemoveElementAtComputation;

import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class RemoveElementAtComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return RemoveElementAtComputation.class;
   }

   public RemoveElementAtComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final RemoveElementAtComputation source;
      final ComputationCompiler address_compiler, target_compiler;
      final Address collection_address;
      final Register result, target_index;

      source = (RemoveElementAtComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      target_compiler = new ComputationCompiler(compiler);

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

      source.get_index().get_visited_by(target_compiler);

      target_compiler.generate_address();

      assimilate(target_compiler);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.RemoveAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            target_compiler.get_address(),
            new Size(result_as_address),
            result_as_address
         )
      );
   }
}
