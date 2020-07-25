package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.World;

public class ComputationCompiler extends FateVisitor
{
   protected final World wyrd_world;
   protected final List<Instruction> pre_computation_instructions;

   public ComputationCompiler (final World wyrd_world)
   {
      this.wyrd_world = wyrd_world;

      pre_computation_instructions = new ArrayList<Instruction>();
   }

   @Override
   public void visit_at_reference
   (
      final tonkadur.fate.v1.lang.valued_node.AtReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_cast
   (
      final tonkadur.fate.v1.lang.valued_node.Cast n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_cond_value
   (
      final tonkadur.fate.v1.lang.valued_node.CondValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_constant
   (
      final tonkadur.fate.v1.lang.valued_node.Constant n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_count_operator
   (
      final tonkadur.fate.v1.lang.valued_node.CountOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_field_reference
   (
      final tonkadur.fate.v1.lang.valued_node.FieldReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_if_else_value
   (
      final tonkadur.fate.v1.lang.valued_node.IfElseValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_is_member_operator
   (
      final tonkadur.fate.v1.lang.valued_node.IsMemberOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_macro_value_call
   (
      final tonkadur.fate.v1.lang.valued_node.MacroValueCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_newline
   (
      final tonkadur.fate.v1.lang.valued_node.Newline n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_operation
   (
      final tonkadur.fate.v1.lang.valued_node.Operation n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_paragraph
   (
      final tonkadur.fate.v1.lang.valued_node.Paragraph n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_parameter_reference
   (
      final tonkadur.fate.v1.lang.valued_node.ParameterReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_ref_operator
   (
      final tonkadur.fate.v1.lang.valued_node.RefOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_text_with_effect
   (
      final tonkadur.fate.v1.lang.valued_node.TextWithEffect n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_value_to_rich_text
   (
      final tonkadur.fate.v1.lang.valued_node.ValueToRichText n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.valued_node.VariableReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }
}
