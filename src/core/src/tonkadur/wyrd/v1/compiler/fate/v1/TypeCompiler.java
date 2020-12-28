package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.HashMap;
import java.util.Map;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.type.*;

import tonkadur.wyrd.v1.lang.World;


public class TypeCompiler
{
   /* Utility Class */
   private TypeCompiler () { }

   public static Type compile
   (
      final Compiler compiler,
      tonkadur.fate.v1.lang.type.Type fate_type
   )
   throws Error
   {
      if (fate_type instanceof tonkadur.fate.v1.lang.type.DictType)
      {
         return
            compile_dict_type
            (
               compiler,
               (tonkadur.fate.v1.lang.type.DictType) fate_type
            );
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.ConsType)
      {
         return DictType.WILD;
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.CollectionType)
      {
         return
            compile_collection_type
            (
               compiler,
               (tonkadur.fate.v1.lang.type.CollectionType) fate_type
            );
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.PointerType)
      {
         return
            new PointerType
            (
               compile
               (
                  compiler,
                  (
                     (tonkadur.fate.v1.lang.type.PointerType)
                     fate_type
                  ).get_referenced_type()
               )
            );
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.LambdaType)
      {
         return Type.INT;
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.SequenceType)
      {
         return Type.INT;
      }

      fate_type = fate_type.get_base_type();

      if (fate_type.equals(tonkadur.fate.v1.lang.type.Type.BOOL))
      {
         return Type.BOOL;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.Type.FLOAT))
      {
         return Type.FLOAT;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.Type.INT))
      {
         return Type.INT;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.Type.RICH_TEXT))
      {
         return Type.RICH_TEXT;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.Type.STRING))
      {
         return Type.STRING;
      }

      System.err.println("[P] Unknown basic fate type '" + fate_type + "'.");

      return null;
   }

   protected static Type compile_dict_type
   (
      final Compiler compiler,
      final tonkadur.fate.v1.lang.type.DictType fate_dict_type
   )
   throws Error
   {
      DictType result;
      final Map<String, Type> fields;

      result = compiler.world().get_dict_type(fate_dict_type.get_name());

      if (result != null)
      {
         return result;
      }

      fields = new HashMap<String, Type>();

      for
      (
         final Map.Entry<String, tonkadur.fate.v1.lang.type.Type> field:
            fate_dict_type.get_fields()
      )
      {
         fields.put
         (
            field.getKey(),
            compile(compiler, field.getValue())
         );
      }

      result = new DictType(fate_dict_type.get_name(), fields);

      compiler.world().add_dict_type(result);

      return result;
   }

   protected static Type compile_collection_type
   (
      final Compiler compiler,
      final tonkadur.fate.v1.lang.type.CollectionType fate_collection_type
   )
   throws Error
   {
      final tonkadur.fate.v1.lang.type.Type fate_content_type;

      fate_content_type =
         fate_collection_type.get_content_type().get_base_type();

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.Type.BOOL))
      {
         return MapType.MAP_TO_BOOL;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.Type.FLOAT))
      {
         return MapType.MAP_TO_FLOAT;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.Type.INT))
      {
         return MapType.MAP_TO_INT;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.Type.STRING))
      {
         return MapType.MAP_TO_STRING;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.Type.RICH_TEXT))
      {
         return MapType.MAP_TO_RICH_TEXT;
      }

      if (fate_content_type instanceof tonkadur.fate.v1.lang.type.LambdaType)
      {
         return MapType.MAP_TO_INT;
      }

      if (fate_content_type instanceof tonkadur.fate.v1.lang.type.SequenceType)
      {
         return MapType.MAP_TO_INT;
      }

      return new MapType(compile(compiler, fate_content_type));
   }
}
