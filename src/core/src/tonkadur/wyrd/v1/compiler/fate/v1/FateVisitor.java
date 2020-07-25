package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.wyrd.v1.error.UnhandledASTElementException;

import tonkadur.fate.v1.lang.meta.NodeVisitor;

public abstract class FateVisitor implements NodeVisitor
{
   /* Instruction Nodes */
   public void visit_add_element
   (
      final tonkadur.fate.v1.lang.instruction.AddElement ae
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_assert (final tonkadur.fate.v1.lang.instruction.Assert a)
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_clear (final tonkadur.fate.v1.lang.instruction.Clear c)
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_cond_instruction
   (
      final tonkadur.fate.v1.lang.instruction.CondInstruction ci
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_display (final tonkadur.fate.v1.lang.instruction.Display n)
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_event_call
   (
      final tonkadur.fate.v1.lang.instruction.EventCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_if_else_instruction
   (
      final tonkadur.fate.v1.lang.instruction.IfElseInstruction n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_if_instruction
   (
      final tonkadur.fate.v1.lang.instruction.IfInstruction n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_instruction_list
   (
      final tonkadur.fate.v1.lang.instruction.InstructionList n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_macro_call
   (
      final tonkadur.fate.v1.lang.instruction.MacroCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_player_choice
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoice n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_player_choice_list
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoiceList n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_remove_all_of_element
   (
      final tonkadur.fate.v1.lang.instruction.RemoveAllOfElement n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_remove_element
   (
      final tonkadur.fate.v1.lang.instruction.RemoveElement n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_sequence_call
   (
      final tonkadur.fate.v1.lang.instruction.SequenceCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_set_value
   (
      final tonkadur.fate.v1.lang.instruction.SetValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   /* Valued Nodes */
   public void visit_at_reference
   (
      final tonkadur.fate.v1.lang.valued_node.AtReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_cast
   (
      final tonkadur.fate.v1.lang.valued_node.Cast n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_cond_value
   (
      final tonkadur.fate.v1.lang.valued_node.CondValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_constant
   (
      final tonkadur.fate.v1.lang.valued_node.Constant n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_count_operator
   (
      final tonkadur.fate.v1.lang.valued_node.CountOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_field_reference
   (
      final tonkadur.fate.v1.lang.valued_node.FieldReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_if_else_value
   (
      final tonkadur.fate.v1.lang.valued_node.IfElseValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_is_member_operator
   (
      final tonkadur.fate.v1.lang.valued_node.IsMemberOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_macro_value_call
   (
      final tonkadur.fate.v1.lang.valued_node.MacroValueCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_newline
   (
      final tonkadur.fate.v1.lang.valued_node.Newline n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_operation
   (
      final tonkadur.fate.v1.lang.valued_node.Operation n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_paragraph
   (
      final tonkadur.fate.v1.lang.valued_node.Paragraph n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_parameter_reference
   (
      final tonkadur.fate.v1.lang.valued_node.ParameterReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_ref_operator
   (
      final tonkadur.fate.v1.lang.valued_node.RefOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_text_with_effect
   (
      final tonkadur.fate.v1.lang.valued_node.TextWithEffect n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_value_to_rich_text
   (
      final tonkadur.fate.v1.lang.valued_node.ValueToRichText n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.valued_node.VariableReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }
}
