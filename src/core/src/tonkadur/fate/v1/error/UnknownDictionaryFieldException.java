package tonkadur.fate.v1.error;

import java.util.Map;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.DictType;
import tonkadur.fate.v1.lang.type.Type;

public class UnknownDictionaryFieldException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String field_name;
   protected final DictType dict;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public UnknownDictionaryFieldException
   (
      final Origin origin,
      final String field_name,
      final DictType dict
   )
   {
      super(ErrorLevel.ERROR, ErrorCategory.INVALID_USE, origin);
      this.field_name = field_name;
      this.dict = dict;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Unknown field '");
      sb.append(field_name);
      sb.append("' for dictionary type '");
      sb.append(dict.get_name());
      sb.append("'.");
      sb.append(System.lineSeparator());
      sb.append("Available fields are:");

      for (final Map.Entry<String, Type> field: dict.get_fields())
      {
         sb.append(System.lineSeparator());
         sb.append("- ");
         sb.append(field.getKey());
         sb.append(": ");
         sb.append(field.getValue().get_name());
      }

      return sb.toString();
   }
}
