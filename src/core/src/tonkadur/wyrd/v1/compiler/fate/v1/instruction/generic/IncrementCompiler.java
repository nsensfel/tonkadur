package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Increment;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;

public class IncrementCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Increment.class;
   }

   public IncrementCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   /*
    * Fate: (set_value address value)
    * Wyrd: (set_value address value)
    */
   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Increment source;
      final ComputationCompiler address_cc;

      source = (Increment) instruction;

      address_cc = new ComputationCompiler(compiler);

      source.get_reference().get_visited_by(address_cc);

      if (address_cc.has_init())
      {
         result.add(address_cc.get_init());
      }

      result.add
      (
         new tonkadur.wyrd.v1.lang.instruction.SetValue
         (
            address_cc.get_address(),
            Operation.plus
            (
               address_cc.get_computation(),
               Constant.ONE
            )
         )
      );

      address_cc.release_registers(result);
   }
}
