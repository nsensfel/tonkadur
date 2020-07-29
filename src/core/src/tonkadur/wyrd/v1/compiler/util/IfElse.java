package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.IfElseComputation;

import tonkadur.wyrd.v1.lang.instruction.SetPC;

public class IfElse
{
   /*
    * Computation boolean condition
    * Instruction if_true
    * Instruction if_false
    *
    * (set .pc (ifelse condition (label if_true_label) (label if_false_label)))
    * (mark if_true_label if_true)
    * (mark_after if_false_label (set .pc (label end_label)))
    * (mark_after end_label if_false)
    *
    */
   public static Instruction generate
   (
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Computation condition,
      final Instruction if_true,
      final Instruction if_false
   )
   {
      final String if_true_label, if_false_label, end_label;
      final List<Instruction> result;

      if_true_label = assembler.generate_label("<ifelse#if_true_label>");
      if_false_label = assembler.generate_label("<ifelse#if_false_label>");
      end_label = assembler.generate_label("<ifelse#end_label>");

      result = new ArrayList<Instruction>();

      result.add
      (
         new SetPC
         (
            new IfElseComputation
            (
               condition,
               assembler.get_label_constant(if_true_label),
               assembler.get_label_constant(if_false_label)
            )
         )
      );

      result.add(assembler.mark(if_true_label, if_true));
      result.add
      (
         assembler.mark_after
         (
            new SetPC(assembler.get_label_constant(end_label)),
            if_false_label
         )
      );
      result.add
      (
         assembler.mark_after
         (
            new SetPC(assembler.get_label_constant(end_label)),
            if_false_label
         )
      );
      result.add(assembler.mark_after(if_false, end_label));

      return assembler.merge(result);
   }
}
