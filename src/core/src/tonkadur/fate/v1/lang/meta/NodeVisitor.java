package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.instruction.*;
import tonkadur.fate.v1.lang.valued_node.*;

public interface NodeVisitor
{
   public void visit_world (final World w)
   throws Throwable;

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

   /* Valued Nodes */
   public void visit_at_reference (final AtReference n)
   throws Throwable;

   public void visit_cast (final Cast n)
   throws Throwable;

   public void visit_cond_value (final CondValue n)
   throws Throwable;

   public void visit_constant (final Constant n)
   throws Throwable;

   public void visit_count_operator (final CountOperator n)
   throws Throwable;

   public void visit_field_reference (final FieldReference n)
   throws Throwable;

   public void visit_if_else_value (final IfElseValue n)
   throws Throwable;

   public void visit_is_member_operator (final IsMemberOperator n)
   throws Throwable;

   public void visit_macro_value_call (final MacroValueCall n)
   throws Throwable;

   public void visit_newline (final Newline n)
   throws Throwable;

   public void visit_operation (final Operation n)
   throws Throwable;

   public void visit_paragraph (final Paragraph n)
   throws Throwable;

   public void visit_parameter_reference (final ParameterReference n)
   throws Throwable;

   public void visit_ref_operator (final RefOperator n)
   throws Throwable;

   public void visit_text_with_effect (final TextWithEffect n)
   throws Throwable;

   public void visit_value_to_rich_text (final ValueToRichText n)
   throws Throwable;

   public void visit_variable_reference (final VariableReference n)
   throws Throwable;

}
