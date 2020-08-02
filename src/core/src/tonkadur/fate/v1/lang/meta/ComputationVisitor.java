package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.computation.*;

public interface ComputationVisitor
{
   public void visit_at_reference (final AtReference n)
   throws Throwable;

   public void visit_access (final Access n)
   throws Throwable;

   public void visit_new (final New n)
   throws Throwable;

   public void visit_cast (final Cast n)
   throws Throwable;

   public void visit_cond_value (final CondValue n)
   throws Throwable;

   public void visit_switch_value (final SwitchValue n)
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

   public void visit_index_of_operator (final IndexOfOperator n)
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
