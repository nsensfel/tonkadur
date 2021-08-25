package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.computation.*;

public interface ComputationVisitor
{
   public void visit_field_access (final FieldAccess n)
   throws Throwable;

   public void visit_cast (final Cast n)
   throws Throwable;

   public void visit_default (final Default n)
   throws Throwable;

   public void visit_cond_value (final CondValue n)
   throws Throwable;

   public void visit_switch_value (final SwitchValue n)
   throws Throwable;

   public void visit_constant (final Constant n)
   throws Throwable;

   public void visit_lambda_expression (final LambdaExpression n)
   throws Throwable;

   public void visit_let (final Let n)
   throws Throwable;

   public void visit_sequence_reference (final SequenceReference n)
   throws Throwable;

   public void visit_paragraph (final Paragraph n)
   throws Throwable;

   public void visit_text_with_effect (final TextWithEffect n)
   throws Throwable;

   public void visit_value_to_text (final ValueToText n)
   throws Throwable;

   public void visit_variable_reference (final VariableReference n)
   throws Throwable;

   public void visit_set_fields (final SetFieldsComputation n)
   throws Throwable;

   public void visit_extra_computation (final ExtraComputationInstance n)
   throws Throwable;

   public void visit_generic_computation (final GenericComputation n)
   throws Throwable;
}
