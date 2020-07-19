package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class UnknownExtensionContentException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String function_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public UnknownExtensionContentException
   (
      final Origin origin,
      final String function_name
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.UNKNOWN,
         origin
      );

      this.function_name = function_name;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Unknown extension content '");
      sb.append(function_name);
      sb.append("'. ");

      return sb.toString();
   }
}
