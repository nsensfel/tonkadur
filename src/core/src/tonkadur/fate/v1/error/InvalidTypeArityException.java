package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

public class InvalidTypeArityException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type base_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public InvalidTypeArityException
   (
      final Origin call_origin,
      final Type base_type
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.base_type = base_type;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Type ");
      sb.append(base_type.toString());
      sb.append(" takes ");
      sb.append(base_type.get_parameters().size());
      sb.append(" type(s) as parameter(s).");

      return sb.toString();
   }
}
