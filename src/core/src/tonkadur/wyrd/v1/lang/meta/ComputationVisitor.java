package tonkadur.wyrd.v1.lang.meta;

import tonkadur.wyrd.v1.lang.computation.*;

public interface ComputationVisitor
{
   public void visit_add_rich_text_effect (final AddRichTextEffect n)
   throws Throwable;

   public void visit_cast (final Cast n)
   throws Throwable;

   public void visit_constant (final Constant n)
   throws Throwable;

   public void visit_if_else_computation (final IfElseComputation n)
   throws Throwable;

   public void visit_new (final New n)
   throws Throwable;

   public void visit_newline (final Newline n)
   throws Throwable;

   public void visit_operation (final Operation n)
   throws Throwable;

   public void visit_address (final Address n)
   throws Throwable;

   public void visit_relative_address (final RelativeAddress n)
   throws Throwable;

   public void visit_rich_text (final RichText n)
   throws Throwable;

   public void visit_size (final Size n)
   throws Throwable;

   public void visit_value_of (final ValueOf n)
   throws Throwable;
}
