package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.instruction.*;

import tonkadur.fate.v1.lang.instruction.generic.ExtraInstruction;

public interface InstructionVisitor
{
   /* Instruction Nodes */
   public void visit_assert (final Assert n)
   throws Throwable;

   public void visit_while (final While n)
   throws Throwable;

   public void visit_do_while (final DoWhile n)
   throws Throwable;

   public void visit_for (final For n)
   throws Throwable;

   public void visit_for_each (final ForEach n)
   throws Throwable;

   public void visit_cond_instruction (final CondInstruction n)
   throws Throwable;

   public void visit_switch_instruction (final SwitchInstruction n)
   throws Throwable;

   public void visit_display (final Display n)
   throws Throwable;

   public void visit_if_else_instruction (final IfElseInstruction n)
   throws Throwable;

   public void visit_if_instruction (final IfInstruction n)
   throws Throwable;

   public void visit_instruction_list (final InstructionList n)
   throws Throwable;

   public void visit_prompt_integer (final PromptInteger n)
   throws Throwable;

   public void visit_prompt_string (final PromptString n)
   throws Throwable;

   public void visit_prompt_command (final PromptCommand n)
   throws Throwable;

   public void visit_player_choice (final PlayerChoice n)
   throws Throwable;

   public void visit_text_option (final TextOption n)
   throws Throwable;

   public void visit_event_option (final EventOption n)
   throws Throwable;

   public void visit_sequence_call (final SequenceCall n)
   throws Throwable;

   public void visit_sequence_jump (final SequenceJump n)
   throws Throwable;

   public void visit_local_variable (final LocalVariable n)
   throws Throwable;

   public void visit_set_fields (final SetFields n)
   throws Throwable;

   public void visit_generic_instruction (final GenericInstruction n)
   throws Throwable;
}
