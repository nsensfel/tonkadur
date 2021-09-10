package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class DangerouslyNamedUserContentException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public DangerouslyNamedUserContentException
   (
      final Origin origin,
      final String name
   )
   {
      super(ErrorLevel.WARNING, ErrorCategory.RECOMMENDATION, origin);

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

      sb.append("In order to ensure support in future Fate versions, user");
      sb.append("-created types, computations, and instructions should be");
      sb.append(" prefixed by '#'.");
      sb.append(System.lineSeparator());
      sb.append("The name \"");
      sb.append(name);
      sb.append("\" is thus not recommended.");

      return sb.toString();
   }
}
