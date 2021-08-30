package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.SubList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class SubListCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return SubList.class;
   }

   public SubListCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final SubList source;
      final ComputationCompiler address_compiler, start_compiler, end_compiler;
      final Register result_holder;

      source = (SubList) instruction;

      address_compiler = new ComputationCompiler(compiler);
      start_compiler = new ComputationCompiler(compiler);
      end_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      source.get_start_index().get_visited_by(start_compiler);

      if (start_compiler.has_init())
      {
         result.add(start_compiler.get_init());
      }

      source.get_end_index().get_visited_by(end_compiler);

      if (end_compiler.has_init())
      {
         result.add(end_compiler.get_init());
      }

      result_holder =
         compiler.registers().reserve
         (
            address_compiler.get_computation().get_type(),
            result
         );

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.SubList.generate
         (
            compiler.registers(),
            compiler.assembler(),
            start_compiler.get_computation(),
            end_compiler.get_computation(),
            address_compiler.get_address(),
            result_holder.get_address()
         )
      );

      result.add
      (
         new SetValue
         (
            address_compiler.get_address(),
            result_holder.get_value()
         )
      );

      compiler.registers().release(result_holder, result);

      address_compiler.release_registers(result);
      start_compiler.release_registers(result);
      end_compiler.release_registers(result);
   }
}
