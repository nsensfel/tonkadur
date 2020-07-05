package tonkadur.fate.v1.lang;

import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.UnknownDictionaryFieldException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class DictType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Map<String, Type> field_types;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public DictType
   (
      final Origin origin,
      final Map<String, Type> field_types,
      final String name
   )
   {
      super(origin, Type.DICT, name);

      this.field_types = field_types;
   }

   /**** Accessors ************************************************************/
   public Type get_field_type (final Origin call_origin, final String t)
   throws UnknownDictionaryFieldException
   {
      final Type result;

      result = field_types.get(t);

      if (result == null)
      {
         ErrorManager.handle
         (
            new UnknownDictionaryFieldException(call_origin, t, this)
         );

         return Type.ANY;
      }

      return result;
   }

   public Set<Map.Entry<String, Type>> get_fields ()
   {
      return field_types.entrySet();
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof DictType)
      {
         final DictType dt;

         dt = (DictType) t;

         for (final Map.Entry<String, Type> own_field: get_fields())
         {
            final Type dt_field_t;

            dt_field_t = dt.field_types.get(own_field.getKey());

            if
            (
               (dt_field_t == null)
               || !(own_field.getValue().can_be_used_as(dt_field_t))
            )
            {
               return false;
            }
         }

         return true;

      }

      return false;
   }

   /*
    * This is for the very special case where a type is used despite not being
    * even a sub-type of the expected one. Using this rather expensive function,
    * the most restrictive shared type will be returned. If no such type exists,
    * the ANY time is returned.
    */
   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      final Map<String, Type> result_field_types;
      final Set<String> result_field_names;
      final DictType dt;

      if (!(de instanceof DictType))
      {
         return Type.ANY;
      }

      dt = (DictType) de;

      result_field_names = new HashSet<String>();
      result_field_types = new HashMap<String, Type>();

      result_field_names.addAll(field_types.keySet());
      result_field_names.addAll(dt.field_types.keySet());

      for (final String field_name: result_field_names)
      {
         final Type this_type;
         final Type other_type;
         Type result_field_type;

         this_type = field_types.get(field_name);
         other_type = dt.field_types.get(field_name);

         if (this_type == null)
         {
            result_field_type = other_type;
         }
         else if (other_type == null)
         {
            result_field_type = this_type;
         }
         else
         {
            result_field_type =
               (Type) this_type.generate_comparable_to(other_type);
         }

         result_field_types.put(field_name, result_field_type);
      }

      return new DictType(get_origin(), result_field_types, name);
   }


   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Dict ");

      for (final Map.Entry<String, Type> field: get_fields())
      {
         sb.append(System.lineSeparator());
         sb.append("(field ");
         sb.append(field.getKey());
         sb.append(" ");
         sb.append(field.getValue().toString());
         sb.append(")");
      }
      sb.append(System.lineSeparator());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }
}
