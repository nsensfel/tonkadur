package tonkadur.jsonexport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.instruction.*;

public class InstructionCompiler implements InstructionVisitor
{
   protected JSONObject result;

   public void visit_add_choice (final AddChoice n)
   throws Throwable
   {
      final ComputationCompiler label_cc, address_cc;

      label_cc = new ComputationCompiler();
      address_cc = new ComputationCompiler();

      n.get_label().get_visited_by(label_cc);
      n.get_address().get_visited_by(address_cc);

      result = new JSONObject();

      result.put("category", "add_choice");
      result.put("label", label_cc.get_result());
      result.put("address", address_cc.get_result());
   }

   public void visit_assert (final Assert n)
   throws Throwable
   {
      final ComputationCompiler cond_cc, msg_cc;

      cond_cc = new ComputationCompiler();
      msg_cc = new ComputationCompiler();

      n.get_condition().get_visited_by(cond_cc);
      n.get_message().get_visited_by(msg_cc);

      result = new JSONObject();

      result.put("category", "assert");
      result.put("condition", cond_cc.get_result());
      result.put("message", msg_cc.get_result());
   }

   public void visit_display (final Display n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_content().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "display");
      result.put("content", cc.get_result());
   }

   public void visit_end (final End n)
   throws Throwable
   {
      result = new JSONObject();

      result.put("category", "end");
   }

   public void visit_event_call (final EventCall n)
   throws Throwable
   {
      final JSONArray params;

      params = new JSONArray();

      for (final Computation c: n.get_parameters())
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler();

         c.get_visited_by(cc);

         params.add(cc.get_result());
      }

      result = new JSONObject();

      result.put("category", "event_call");
      result.put("event", n.get_name());
      result.put("parameters", params);
   }

   public void visit_remove (final Remove n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_reference().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "remove");
      result.put("reference", cc.get_result());
   }

   public void visit_resolve_choices (final ResolveChoices n)
   throws Throwable
   {
      result = new JSONObject();

      result.put("category", "resolve_choices");
   }

   public void visit_set_pc (final SetPC n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_value().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "set_pc");
      result.put("value", cc.get_result());
   }

   public void visit_set_value (final SetValue n)
   throws Throwable
   {
      final ComputationCompiler ref_cc, val_cc;

      ref_cc = new ComputationCompiler();
      val_cc = new ComputationCompiler();

      n.get_reference().get_visited_by(ref_cc);
      n.get_value().get_visited_by(val_cc);

      result = new JSONObject();

      result.put("category", "set_value");
      result.put("reference", ref_cc.get_result());
      result.put("value", val_cc.get_result());
   }

   public JSONObject get_result ()
   {
      return result;
   }
}
