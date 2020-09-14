package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.instruction.*;

public interface InstructionVisitor
{
   /* Instruction Nodes */
   public void visit_add_element (final AddElement n)
   throws Throwable;

   public void visit_add_element_at (final AddElementAt n)
   throws Throwable;

   public void visit_add_elements_of (final AddElementsOf n)
   throws Throwable;

   public void visit_assert (final Assert n)
   throws Throwable;

   public void visit_break (final Break n)
   throws Throwable;

   public void visit_end (final End n)
   throws Throwable;

   public void visit_done (final Done n)
   throws Throwable;

   public void visit_free (final Free n)
   throws Throwable;

   public void visit_while (final While n)
   throws Throwable;

   public void visit_do_while (final DoWhile n)
   throws Throwable;

   public void visit_for (final For n)
   throws Throwable;

   public void visit_remove_element_at (final RemoveElementAt n)
   throws Throwable;

   public void visit_for_each (final ForEach n)
   throws Throwable;

   public void visit_clear (final Clear n)
   throws Throwable;

   public void visit_map (final Map n)
   throws Throwable;

   public void visit_merge (final Merge n)
   throws Throwable;

   public void visit_filter (final Filter n)
   throws Throwable;

   public void visit_sublist (final SubList n)
   throws Throwable;

   public void visit_partition (final Partition n)
   throws Throwable;

   public void visit_sort (final Sort n)
   throws Throwable;

   public void visit_indexed_map (final IndexedMap c)
   throws Throwable;

   public void visit_shuffle (final Shuffle c)
   throws Throwable;

   public void visit_pop_element (final PopElement c)
   throws Throwable;

   public void visit_push_element (final PushElement c)
   throws Throwable;

   public void visit_reverse_list (final ReverseList n)
   throws Throwable;

   public void visit_cond_instruction (final CondInstruction n)
   throws Throwable;

   public void visit_switch_instruction (final SwitchInstruction n)
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

   public void visit_prompt_integer (final PromptInteger n)
   throws Throwable;

   public void visit_prompt_string (final PromptString n)
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

   public void visit_sequence_jump (final SequenceJump n)
   throws Throwable;

   public void visit_local_variable (final LocalVariable n)
   throws Throwable;

   public void visit_set_value (final SetValue n)
   throws Throwable;

   public void visit_set_fields (final SetFields n)
   throws Throwable;
}
