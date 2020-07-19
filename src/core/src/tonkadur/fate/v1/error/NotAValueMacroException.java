package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class NotAValueMacroException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String macro_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public NotAValueMacroException (final Origin origin, final String macro_name)
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.INVALID_USE,
         origin
      );

      this.macro_name = macro_name;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Macro '");
      sb.append(macro_name);
      sb.append
      (
         "' is not defined as a single value and thus cannot be used as one."
      );
      sb.append(System.lineSeparator());
      sb.append("Does it contain instructions?");
      sb.append(System.lineSeparator());
      sb.append("Is it a sequence of multiple values?");

      return sb.toString();
   }
}
