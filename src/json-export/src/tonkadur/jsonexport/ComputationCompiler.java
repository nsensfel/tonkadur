package tonkadur.jsonexport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.*;

public class ComputationCompiler implements ComputationVisitor
{
   protected JSONObject result;

   public void visit_add_text_effect (final AddTextEffect n)
   throws Throwable
   {
      final JSONArray params, content;

      params = new JSONArray();
      content = new JSONArray();

      for (final Computation c: n.get_content())
      {
         final ComputationCompiler content_cc;

         content_cc = new ComputationCompiler();

         c.get_visited_by(content_cc);

         content.add(content_cc.get_result());
      }

      for (final Computation c: n.get_effect_parameters())
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler();

         c.get_visited_by(param_cc);

         params.add(param_cc.get_result());
      }

      result = new JSONObject();

      result.put("category", "add_text_effect");
      result.put("effect", n.get_effect_name());
      result.put("parameters", params);
      result.put("content", content);
   }

   public void visit_cast (final Cast n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_parent().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "cast");
      result.put("from", Translator.compile_type(n.get_parent().get_type()));
      result.put("to", Translator.compile_type(n.get_type()));
      result.put("content", cc.get_result());
   }

   public void visit_constant (final Constant n)
   throws Throwable
   {
      result = new JSONObject();

      result.put("category", "constant");
      result.put("type", Translator.compile_type(n.get_type()));
      result.put("value", n.get_as_string());
   }

   public void visit_if_else_computation (final IfElseComputation n)
   throws Throwable
   {
      final ComputationCompiler cond_cc, if_true_cc, if_false_cc;

      cond_cc = new ComputationCompiler();
      if_true_cc = new ComputationCompiler();
      if_false_cc = new ComputationCompiler();

      n.get_condition().get_visited_by(cond_cc);
      n.get_if_true().get_visited_by(if_true_cc);
      n.get_if_false().get_visited_by(if_false_cc);

      result = new JSONObject();

      result.put("category", "if_else");
      result.put("type", Translator.compile_type(n.get_type()));
      result.put("condition", cond_cc.get_result());
      result.put("if_true", if_true_cc.get_result());
      result.put("if_false", if_false_cc.get_result());
   }

   public void visit_get_allocable_address (final GetAllocableAddress n)
   throws Throwable
   {
      result = new JSONObject();

      result.put("category", "get_allocable_address");
      result.put("target", Translator.compile_type(n.get_target_type()));
   }

   public void visit_newline (final Newline n)
   throws Throwable
   {
      result = new JSONObject();

      result.put("category", "newline");
   }

   public void visit_get_last_choice_index (final GetLastChoiceIndex n)
   throws Throwable
   {
      result = new JSONObject();

      result.put("category", "last_choice_index");
   }

   public void visit_operation (final Operation n)
   throws Throwable
   {
      ComputationCompiler cc;

      cc = new ComputationCompiler();

      result = new JSONObject();

      n.get_first_parameter().get_visited_by(cc);

      result.put("category", "operation");
      result.put("operator", n.get_operator());
      result.put("type", Translator.compile_type(n.get_type()));
      result.put("x", cc.get_result());

      if (n.get_second_parameter() != null)
      {
         cc = new ComputationCompiler();

         n.get_second_parameter().get_visited_by(cc);

         result.put("y", cc.get_result());
      }
   }

   public void visit_address (final Address n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_address().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "address");
      result.put("target_type", Translator.compile_type(n.get_type()));
      result.put("address", cc.get_result());
   }

   public void visit_relative_address (final RelativeAddress n)
   throws Throwable
   {
      final ComputationCompiler cc, param_cc;

      cc = new ComputationCompiler();
      param_cc = new ComputationCompiler();

      n.get_address().get_visited_by(cc);
      n.get_member().get_visited_by(param_cc);

      result = new JSONObject();

      result.put("category", "relative_address");
      result.put("type", Translator.compile_type(n.get_type()));
      result.put("base", cc.get_result());
      result.put("extra", param_cc.get_result());
   }

   public void visit_text (final Text n)
   throws Throwable
   {
      final JSONArray content;

      content = new JSONArray();

      for (final Computation c: n.get_content())
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler();

         c.get_visited_by(cc);

         content.add(cc.get_result());
      }

      result = new JSONObject();

      result.put("category", "text");
      result.put("content", content);
   }

   public void visit_size (final Size n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_collection().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "size");
      result.put("reference", cc.get_result());
   }

   public void visit_extra_computation (final ExtraComputation n)
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

      result.put("category", "extra_computation");
      result.put("name", n.get_name());
      result.put("parameters", params);
   }

   public void visit_value_of (final ValueOf n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler();

      n.get_parent().get_visited_by(cc);

      result = new JSONObject();

      result.put("category", "value_of");
      result.put("type", Translator.compile_type(n.get_type()));
      result.put("reference", cc.get_result());
   }

   public JSONObject get_result ()
   {
      return result;
   }
}
