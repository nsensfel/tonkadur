package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class UpdatingIllegalVariableFromChoiceException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String var_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public UpdatingIllegalVariableFromChoiceException
   (
      final Origin origin,
      final String var_name
   )
   {
      super(ErrorLevel.ERROR, ErrorCategory.INVALID_USE, origin);

      this.var_name = var_name;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Updating a variable that wasn't initialized within the ");
      sb.append("current choice construct ('");
      sb.append(var_name);
      sb.append("').");

      return sb.toString();
   }
}
