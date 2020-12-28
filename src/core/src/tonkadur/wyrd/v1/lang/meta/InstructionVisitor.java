package tonkadur.wyrd.v1.lang.meta;

import tonkadur.wyrd.v1.lang.instruction.*;

public interface InstructionVisitor
{
   public void visit_extra_instruction (final ExtraInstruction n)
   throws Throwable;

   public void visit_add_text_option (final AddTextOption n)
   throws Throwable;

   public void visit_add_event_option (final AddEventOption n)
   throws Throwable;

   public void visit_assert (final Assert n)
   throws Throwable;

   public void visit_display (final Display n)
   throws Throwable;

   public void visit_end (final End n)
   throws Throwable;

   public void visit_remove (final Remove n)
   throws Throwable;

   public void visit_resolve_choice (final ResolveChoice n)
   throws Throwable;

   public void visit_set_pc (final SetPC n)
   throws Throwable;

   public void visit_prompt_integer (final PromptInteger n)
   throws Throwable;

   public void visit_prompt_string (final PromptString n)
   throws Throwable;

   public void visit_set_value (final SetValue n)
   throws Throwable;

   public void visit_initialize (final Initialize n)
   throws Throwable;
}
