package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

public class IncorrectReturnTypeException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type given_type;
   protected final Type allowed_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public IncorrectReturnTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Type allowed_type
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.given_type = given_type;
      this.allowed_type = allowed_type;
   }


   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Return type '");
      sb.append(given_type.toString());
      sb.append("'");

      sb.append(" is not allowed here. Use '");
      sb.append(allowed_type.toString());
      sb.append("' instead.");

      return sb.toString();
   }
}
