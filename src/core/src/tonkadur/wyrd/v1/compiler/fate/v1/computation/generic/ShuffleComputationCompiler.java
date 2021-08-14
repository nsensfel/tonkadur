package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.ShuffleComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class ShuffleComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return ShuffleComputation.class;
   }

   public ShuffleComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final ShuffleComputation source;
      final ComputationCompiler address_compiler;
      final Register result;

      source = (ShuffleComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);

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

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.Shuffle.generate
         (
            compiler.registers(),
            compiler.assembler(),
            result_as_address
         )
      );
   }
}
