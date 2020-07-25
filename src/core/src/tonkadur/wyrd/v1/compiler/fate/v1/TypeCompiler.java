package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.type.*;

import tonkadur.wyrd.v1.lang.World;


public class TypeCompiler
{
   /* Utility Class */
   private TypeCompiler () { }

   public static Type compile
   (
      tonkadur.fate.v1.lang.type.Type fate_type,
      final World wyrd_world
   )
   throws Error
   {
      if (fate_type instanceof tonkadur.fate.v1.lang.type.DictType)
      {
         return
            compile_dict_type
            (
               (tonkadur.fate.v1.lang.type.DictType) fate_type,
               wyrd_world
            );
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.CollectionType)
      {
         return
            compile_collection_type
            (
               (tonkadur.fate.v1.lang.type.CollectionType) fate_type,
               wyrd_world
            );
      }

      if (fate_type instanceof tonkadur.fate.v1.lang.type.RefType)
      {
         return Type.POINTER;
      }

      fate_type = fate_type.get_base_type();

      if (fate_type.equals(tonkadur.fate.v1.lang.type.BOOLEAN))
      {
         return Type.BOOLEAN;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.FLOAT))
      {
         return Type.FLOAT;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.INT))
      {
         return Type.INT;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.RICH_TEXT))
      {
         return Type.RICH_TEXT;
      }

      if (fate_type.equals(tonkadur.fate.v1.lang.type.STRING))
      {
         return Type.STRING;
      }

      /* TODO: throw error. */
      return null;
   }

   protected static Type compile_dict_type
   (
      final tonkadur.fate.v1.lang.type.DictType fate_dict_type,
      final World wyrd_world
   )
   throws Error
   {
      DictType result;
      final Map<String, Type> fields;

      result = wyrd_world.get_dict_type(fate_dict_type.get_name());

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
         fields.put(field.getKey(), compile(field.getValue(), wyrd_world));
      }

      result = new DictType(fate_dict_type.get_name(), fields);

      wyrd_world.add_dict_type(result);

      return result;
   }

   protected static Type compile_collection_type
   (
      final tonkadur.fate.v1.lang.type.CollectionType fate_collection_type,
      final World wyrd_world
   )
   throws Error
   {
      final tonkadur.fate.v1.lang.type.Type fate_content_type;

      fate_content_type =
         fate_collection_type.get_content_type().get_base_type();

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.BOOLEAN))
      {
         return MapType.MAP_TO_BOOLEAN;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.FLOAT))
      {
         return MapType.MAP_TO_FLOAT;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.INT))
      {
         return MapType.MAP_TO_INT;
      }

      if (fate_content_type.equals(tonkadur.fate.v1.lang.type.STRING))
      {
         return MapType.MAP_TO_STRING;
      }

      /* TODO: error */

      return null;
   }
}
