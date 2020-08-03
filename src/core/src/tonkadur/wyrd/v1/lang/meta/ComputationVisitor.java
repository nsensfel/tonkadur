package tonkadur.wyrd.v1.lang.meta;

import tonkadur.wyrd.v1.lang.computation.*;

public interface ComputationVisitor
{
   public void visit_add_rich_text_effect (final AddRichTextEffect n)
   throws Exception;

   public void visit_cast (final Cast n)
   throws Exception;

   public void visit_constant (final Constant n)
   throws Exception;

   public void visit_if_else_computation (final IfElseComputation n)
   throws Exception;

   public void visit_new (final New n)
   throws Exception;

   public void visit_newline (final Newline n)
   throws Exception;

   public void visit_operation (final Operation n)
   throws Exception;

   public void visit_ref (final Ref n)
   throws Exception;

   public void visit_relative_ref (final RelativeRef n)
   throws Exception;

   public void visit_rich_text (final RichText n)
   throws Exception;

   public void visit_size (final Size n)
   throws Exception;

   public void visit_value_of (final ValueOf n)
   throws Exception;
}
