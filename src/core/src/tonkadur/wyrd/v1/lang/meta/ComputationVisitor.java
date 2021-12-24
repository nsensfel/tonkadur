package tonkadur.wyrd.v1.lang.meta;

import tonkadur.wyrd.v1.lang.computation.*;

public interface ComputationVisitor
{
   public void visit_extra_computation (final ExtraComputation n)
   throws Throwable;

   public void visit_add_text_effect (final AddTextEffect n)
   throws Throwable;

   public void visit_cast (final Cast n)
   throws Throwable;

   public void visit_constant (final Constant n)
   throws Throwable;

   public void visit_if_else_computation (final IfElseComputation n)
   throws Throwable;

   public void visit_newline (final Newline n)
   throws Throwable;

   public void visit_operation (final Operation n)
   throws Throwable;

   public void visit_address (final Address n)
   throws Throwable;

   public void visit_relative_address (final RelativeAddress n)
   throws Throwable;

   public void visit_get_allocable_address (final GetAllocableAddress n)
   throws Throwable;

   public void visit_get_last_choice_index (final GetLastChoiceIndex n)
   throws Throwable;

   public void visit_text (final Text n)
   throws Throwable;

   public void visit_size (final Size n)
   throws Throwable;

   public void visit_value_of (final ValueOf n)
   throws Throwable;
}
