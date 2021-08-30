package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.SetValue;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class SetValueCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return SetValue.class;
   }

   public SetValueCompiler (final Compiler compiler)
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
      final SetValue source;
      final ComputationCompiler value_cc, address_cc;

      source = (SetValue) instruction;

      value_cc = new ComputationCompiler(compiler);
      address_cc = new ComputationCompiler(compiler);

      source.get_value().get_visited_by(value_cc);

      if (value_cc.has_init())
      {
         result.add(value_cc.get_init());
      }

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
            value_cc.get_computation()
         )
      );

      value_cc.release_registers(result);
      address_cc.release_registers(result);
   }
}
