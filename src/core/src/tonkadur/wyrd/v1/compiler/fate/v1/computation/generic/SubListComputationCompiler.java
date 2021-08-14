package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.SubListComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class SubListComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return SubListComputation.class;
   }

   public SubListComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final SubListComputation source;
      final ComputationCompiler address_compiler, start_compiler, end_compiler;
      final Register result;

      source = (SubListComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      start_compiler = new ComputationCompiler(compiler);
      end_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      source.get_start_index().get_visited_by(start_compiler);

      if (start_compiler.has_init())
      {
         init_instructions.add(start_compiler.get_init());
      }

      source.get_end_index().get_visited_by(end_compiler);

      if (end_compiler.has_init())
      {
         init_instructions.add(end_compiler.get_init());
      }

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.SubList.generate
         (
            compiler.registers(),
            compiler.assembler(),
            start_compiler.get_computation(),
            end_compiler.get_computation(),
            address_compiler.get_address(),
            result_as_address
         )
      );

      start_compiler.release_registers(init_instructions);
      end_compiler.release_registers(init_instructions);
      address_compiler.release_registers(init_instructions);
   }
}
