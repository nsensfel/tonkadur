package tonkadur.fate.v1.lang.meta;

import tonkadur.fate.v1.lang.computation.*;

public interface ComputationVisitor
{
   public void visit_at_reference (final AtReference n)
   throws Throwable;

   public void visit_access (final Access n)
   throws Throwable;

   public void visit_access_pointer (final AccessPointer n)
   throws Throwable;

   public void visit_access_as_reference (final AccessAsReference n)
   throws Throwable;

   public void visit_new (final New n)
   throws Throwable;

   public void visit_cast (final Cast n)
   throws Throwable;

   public void visit_cons (final ConsComputation n)
   throws Throwable;

   public void visit_fold (final Fold n)
   throws Throwable;

   public void visit_default (final Default n)
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

   public void visit_size_operator (final SizeOperator n)
   throws Throwable;

   public void visit_lambda_expression (final LambdaExpression n)
   throws Throwable;

   public void visit_lambda_evaluation (final LambdaEvaluation n)
   throws Throwable;

   public void visit_let (final Let n)
   throws Throwable;

   public void visit_is_empty (final IsEmpty n)
   throws Throwable;

   public void visit_newline (final Newline n)
   throws Throwable;

   public void visit_operation (final Operation n)
   throws Throwable;

   public void visit_paragraph (final Paragraph n)
   throws Throwable;

   public void visit_address_operator (final AddressOperator n)
   throws Throwable;

   public void visit_text_with_effect (final TextWithEffect n)
   throws Throwable;

   public void visit_value_to_rich_text (final ValueToRichText n)
   throws Throwable;

   public void visit_variable_reference (final VariableReference n)
   throws Throwable;

   public void visit_car_cdr (final CarCdr n)
   throws Throwable;

   public void visit_add_element (final AddElementComputation n)
   throws Throwable;

   public void visit_add_element_at (final AddElementAtComputation n)
   throws Throwable;

   public void visit_add_elements_of (final AddElementsOfComputation n)
   throws Throwable;

   public void visit_remove_elements_of (final RemoveElementsOfComputation n)
   throws Throwable;

   public void visit_map (final MapComputation n)
   throws Throwable;

   public void visit_indexed_map (final IndexedMapComputation n)
   throws Throwable;

   public void visit_sort (final SortComputation n)
   throws Throwable;

   public void visit_range (final Range n)
   throws Throwable;

   public void visit_remove_all_of_element
   (
      final RemoveAllOfElementComputation n
   )
   throws Throwable;

   public void visit_remove_element_at (final RemoveElementAtComputation n)
   throws Throwable;

   public void visit_remove_element (final RemoveElementComputation n)
   throws Throwable;

   public void visit_reverse_list (final ReverseListComputation n)
   throws Throwable;

   public void visit_shuffle (final ShuffleComputation n)
   throws Throwable;

   public void visit_merge (final MergeComputation n)
   throws Throwable;

   public void visit_indexed_merge (final IndexedMergeComputation n)
   throws Throwable;

   public void visit_filter (final FilterComputation n)
   throws Throwable;

   public void visit_indexed_filter (final IndexedFilterComputation n)
   throws Throwable;

   public void visit_sublist (final SubListComputation n)
   throws Throwable;

   public void visit_partition (final PartitionComputation n)
   throws Throwable;

   public void visit_indexed_partition (final IndexedPartitionComputation n)
   throws Throwable;

   public void visit_push_element (final PushElementComputation n)
   throws Throwable;

   public void visit_pop_element (final PopElementComputation n)
   throws Throwable;

   public void visit_set_fields (final SetFieldsComputation n)
   throws Throwable;
}
