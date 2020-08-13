package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.Variable;

public class DuplicateLocalVariableException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Variable original_var;
   protected final Variable new_var;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public DuplicateLocalVariableException
   (
      final Variable original_var,
      final Variable new_var
   )
   {
      super(ErrorLevel.ERROR, ErrorCategory.INVALID_USE, new_var.get_origin());

      this.original_var = original_var;
      this.new_var = new_var;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Local variable name '");
      sb.append(original_var.get_name());
      sb.append("' already in use. It was originally reserved  at ");
      sb.append(original_var.get_origin().get_location().toString());
      sb.append(".");

      return sb.toString();
   }
}
