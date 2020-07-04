package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class MissingDeclarationException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String type_name;
   protected final String name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public MissingDeclarationException
   (
      final Origin origin,
      final String type_name,
      final String name
   )
   {
      super(ErrorLevel.ERROR, ErrorCategory.MISSING_DECLARATION, origin);
      this.type_name = type_name;
      this.name = name;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.get_context().toString());
      sb.append("Unknown ");
      sb.append(type_name);
      sb.append(" '");
      sb.append(name);
      sb.append("' at ");
      sb.append(origin.get_location().toString());
      sb.append(".");

      return sb.toString();
   }
}
