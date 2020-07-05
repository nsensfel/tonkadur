package tonkadur.fate.v1.error;

import java.util.Map;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class IllegalReferenceNameException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public IllegalReferenceNameException
   (
      final Origin origin,
      final String name
   )
   {
      super(ErrorLevel.ERROR, ErrorCategory.INVALID_USE, origin);
      this.name = name;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Illegal use of '.' ('");
      sb.append(name);
      sb.append("').");

      return sb.toString();
   }
}
