package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.IfElseComputation;

import tonkadur.wyrd.v1.lang.instruction.SetPC;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class If
{
   /*
    * Computation boolean condition
    * Instruction if_true
    *
    * (set .pc (ifelse condition (label if_true_label) (label end_if_label)))
    * (mark_after end_if_label (mark if_true_label instruction))
    *
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation condition,
      final Instruction if_true
   )
   {
      final String if_true_label, end_label;
      final List<Instruction> result;

      if_true_label = assembler.generate_label("<if#if_true_label>");
      end_label = assembler.generate_label("<if#end_label>");

      result = new ArrayList<Instruction>();

      result.add
      (
         new SetPC
         (
            new IfElseComputation
            (
               condition,
               assembler.get_label_constant(if_true_label),
               assembler.get_label_constant(end_label)
            )
         )
      );

      result.add
      (
         assembler.mark_after
         (
            assembler.mark
            (
               if_true_label,
               if_true
            ),
            end_label
         )
      );

      return assembler.merge(result);
   }
}
