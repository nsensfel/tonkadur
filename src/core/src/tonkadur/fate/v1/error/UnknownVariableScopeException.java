package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.VariableScope;

public class UnknownVariableScopeException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public UnknownVariableScopeException
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

      sb.append("Unknown variable scope '");
      sb.append(name);
      sb.append("'.");
      sb.append(System.lineSeparator());
      sb.append("Available fields are:");

      for (final String scope: VariableScope.get_available_scopes())
      {
         sb.append(System.lineSeparator());
         sb.append("- ");
         sb.append(scope);
      }

      return sb.toString();
   }
}
