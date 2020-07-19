package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

public class IncompatibleTypeException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type given_type;
   protected final Type expected_type;
   protected final Type hint;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public IncompatibleTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Type expected_type
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.INCOMPATIBLE,
         call_origin
      );

      this.given_type = given_type;
      this.expected_type = expected_type;
      this.hint = null;
   }

   public IncompatibleTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Type expected_type,
      final Type hint
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.INCOMPATIBLE,
         call_origin
      );

      this.given_type = given_type;
      this.expected_type = expected_type;
      this.hint = hint;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Resulting type '");
      sb.append(given_type.toString());
      sb.append("' ");
      sb.append(" is incompatible with the expected one ('");
      sb.append(expected_type.toString());
      sb.append("').");

      if (hint != null)
      {
         sb.append(System.lineSeparator());
         sb.append("Recommended compatible type: ");
         sb.append(hint.toString());
      }

      return sb.toString();
   }
}
