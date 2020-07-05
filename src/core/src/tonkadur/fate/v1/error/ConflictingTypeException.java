package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.Type;

public class ConflictingTypeException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type given_type;
   protected final Type expected_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public ConflictingTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Type expected_type
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.CONFLICTING,
         call_origin
      );

      this.given_type = given_type;
      this.expected_type = expected_type;
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
      sb.append(given_type.get_name());
      sb.append("' ");
      sb.append(" conflicts with the expected one ('");
      sb.append(expected_type.get_name());
      sb.append("').");

      return sb.toString();
   }
}
