package tonkadur.wyrd.v1.compiler.util;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.instruction.SetPC;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class NOP
{
   /*
    * (mark_after (set .pc (label nop_label)) nop_label)
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler
   )
   {
      return generate(assembler);
   }

   public static Instruction generate
   (
      final InstructionManager assembler
   )
   {
      final String nop_label;

      nop_label = assembler.generate_label("<nop_label>");

      return
         assembler.mark_after
         (
            new SetPC(assembler.get_label_constant(nop_label)),
            nop_label
         );
   }
}
