package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.instruction.*;

public interface InstructionVisitor
{
   /* Instruction Nodes */
   public void visit_add_element (final AddElement ae)
   throws Throwable;

   public void visit_assert (final Assert a)
   throws Throwable;

   public void visit_clear (final Clear c)
   throws Throwable;

   public void visit_cond_instruction (final CondInstruction ci)
   throws Throwable;

   public void visit_display (final Display n)
   throws Throwable;

   public void visit_event_call (final EventCall n)
   throws Throwable;

   public void visit_if_else_instruction (final IfElseInstruction n)
   throws Throwable;

   public void visit_if_instruction (final IfInstruction n)
   throws Throwable;

   public void visit_instruction_list (final InstructionList n)
   throws Throwable;

   public void visit_macro_call (final MacroCall n)
   throws Throwable;

   public void visit_player_choice (final PlayerChoice n)
   throws Throwable;

   public void visit_player_choice_list (final PlayerChoiceList n)
   throws Throwable;

   public void visit_remove_all_of_element (final RemoveAllOfElement n)
   throws Throwable;

   public void visit_remove_element (final RemoveElement n)
   throws Throwable;

   public void visit_sequence_call (final SequenceCall n)
   throws Throwable;

   public void visit_set_value (final SetValue n)
   throws Throwable;
}
