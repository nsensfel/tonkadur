package tonkadur.wyrd.v1.lang.meta;

import tonkadur.wyrd.v1.lang.instruction.*;

public interface InstructionVisitor
{
   public void visit_add_choice (final AddChoice n)
   throws Exception;

   public void visit_assert (final Assert n)
   throws Exception;

   public void visit_display (final Display n)
   throws Exception;

   public void visit_end (final End n)
   throws Exception;

   public void visit_event_call (final EventCall n)
   throws Exception;

   public void visit_remove (final Remove n)
   throws Exception;

   public void visit_resolve_choices (final ResolveChoices n)
   throws Exception;

   public void visit_set_pc (final SetPC n)
   throws Exception;

   public void visit_set_value (final SetValue n)
   throws Exception;
}
