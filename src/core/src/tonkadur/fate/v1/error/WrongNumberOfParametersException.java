package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class WrongNumberOfParametersException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String documentation;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public WrongNumberOfParametersException
   (
      final Origin origin,
      final String documentation
   )
   {
      super(ErrorLevel.ERROR, ErrorCategory.INVALID_USE, origin);

      this.documentation = documentation;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Incorrect parameters, see documentation:");
      sb.append(System.lineSeparator());
      sb.append(documentation);

      return sb.toString();
   }
}
