package tonkadur.jsonexport;

import java.util.Map;

import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import tonkadur.wyrd.v1.lang.World;
import tonkadur.wyrd.v1.lang.type.*;
import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Translator
{
   public static void toJSON (final World wyrd_world, final String output_file)
   throws Throwable
   {
      final PrintWriter out;
      final JSONObject result;

      result = new JSONObject();

      result.put("extensions", get_compiled_extension_list(wyrd_world));
      result.put("structure_types", get_compiled_types(wyrd_world));
      result.put("variables", get_compiled_variables(wyrd_world));
      result.put("sequences", get_compiled_sequence_labels(wyrd_world));
      result.put("code", get_compiled_code(wyrd_world));


      out = new PrintWriter(output_file);
      out.println(result.toJSONString());
      out.close();
   }

   public static JSONArray get_compiled_variables (final World wyrd_world)
   throws Throwable
   {
      final JSONArray result;

      result = new JSONArray();

      for
      (
         final Register e: wyrd_world.get_registers()
      )
      {
         final JSONObject obj;

         obj = new JSONObject();

         obj.put("name", e.get_name());
         obj.put("type", compile_type(e.get_type()));

         result.add(obj);
      }

      return result;
   }

   public static JSONArray get_compiled_sequence_labels (final World wyrd_world)
   throws Throwable
   {
      final JSONArray result;

      result = new JSONArray();

      for
      (
         final Map.Entry<String, Integer> i:
            wyrd_world.get_sequence_labels().entrySet()
      )
      {
         final JSONObject obj;

         obj = new JSONObject();

         obj.put("name", i.getKey());
         obj.put("line", i.getValue());

         result.add(obj);
      }

      return result;
   }

   public static JSONArray get_compiled_extension_list (final World wyrd_world)
   throws Throwable
   {
      final JSONArray result;

      result = new JSONArray();

      for (final String e: wyrd_world.get_required_extensions())
      {
         result.add(e);
      }

      return result;
   }

   public static JSONArray get_compiled_types (final World wyrd_world)
   throws Throwable
   {
      final JSONArray result;

      result = new JSONArray();

      for (final DictType structure: wyrd_world.get_ordered_dict_types())
      {
         final JSONObject type;
         final JSONArray fields;

         fields = new JSONArray();

         for
         (
            final Map.Entry<String, Type> field:
               structure.get_fields().entrySet()
         )
         {
            final JSONObject f;

            f = new JSONObject();

            f.put("name", field.getKey());
            f.put("type", compile_type(field.getValue()));

            fields.add(f);
         }

         type = new JSONObject();

         type.put("name", structure.get_name());
         type.put("fields", fields);

         result.add(type);
      }

      return result;
   }

   public static JSONArray get_compiled_code (final World wyrd_world)
   throws Throwable
   {
      final JSONArray result;

      result = new JSONArray();

      for (final Instruction code_line: wyrd_world.get_code())
      {
         final InstructionCompiler ic;

         ic = new InstructionCompiler();

         code_line.get_visited_by(ic);

         result.add(ic.get_result());
      }

      return result;
   }

   public static JSONObject compile_type (final Type t)
   {
      final JSONObject result;

      result = new JSONObject();

      if (t instanceof DictType)
      {
         result.put("category", "structure");
         result.put("name", t.get_name());
      }
      else if (t instanceof PointerType)
      {
         result.put("category", "pointer");
         result.put
         (
            "target",
            compile_type(((PointerType) t).get_target_type())
         );
      }
      else if (t instanceof MapType)
      {
         result.put("category", "list");
         result.put
         (
            "member_type",
            compile_type(((MapType) t).get_member_type())
         );
      }
      else
      {
         result.put("category", t.get_name());
      }

      return result;
   }
}
