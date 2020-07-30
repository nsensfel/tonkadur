package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.IfElseComputation;

import tonkadur.wyrd.v1.lang.instruction.SetPC;

public class While
{
   /*
    * Computation boolean condition
    * Instruction while_body
    *
    * (mark start_label
    *    (set .pc (ifelse condition (label if_true_label) (label end_label)))
    * )
    * (mark if_true_label while_body)
    * (mark_after end_label (set .pc (label start_label)))
    */
   public static Instruction generate
   (
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Computation condition,
      final Instruction while_body
   )
   {
      final String start_label, if_true_label, end_label;
      final List<Instruction> result;

      start_label = assembler.generate_label("<while#start_label>");
      if_true_label = assembler.generate_label("<while#if_true_label>");
      end_label = assembler.generate_label("<while#end_label>");

      result = new ArrayList<Instruction>();

      result.add
      (
         assembler.mark
         (
            start_label,
            new SetPC
            (
               new IfElseComputation
               (
                  condition,
                  assembler.get_label_constant(if_true_label),
                  assembler.get_label_constant(end_label)
               )
            )
         )
      );

      result.add(assembler.mark(if_true_label, while_body));
      result.add
      (
         assembler.mark_after
         (
            new SetPC(assembler.get_label_constant(start_label)),
            end_label
         )
      );

      return assembler.merge(result);
   }

   /*
    * Instruction cond_init
    * Computation boolean condition
    * Instruction while_body
    *
    * (mark start_label cond_init)
    * (set .pc (ifelse condition (label if_true_label) (label end_label)))
    * (mark if_true_label while_body)
    * (mark_after end_label (set .pc (label start_label)))
    */
   public static Instruction generate
   (
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Instruction cond_init,
      final Computation condition,
      final Instruction while_body
   )
   {
      final String start_label, if_true_label, end_label;
      final List<Instruction> result;

      start_label = assembler.generate_label("<while#start_label>");
      if_true_label = assembler.generate_label("<while#if_true_label>");
      end_label = assembler.generate_label("<while#end_label>");

      result = new ArrayList<Instruction>();

      result.add(assembler.mark(start_label, cond_init));

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

      result.add(assembler.mark(if_true_label, while_body));
      result.add
      (
         assembler.mark_after
         (
            new SetPC(assembler.get_label_constant(start_label)),
            end_label
         )
      );

      return assembler.merge(result);
   }
}
