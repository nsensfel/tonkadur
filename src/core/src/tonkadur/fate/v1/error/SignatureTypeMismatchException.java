package tonkadur.fate.v1.error;

import java.util.List;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

public class SignatureTypeMismatchException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Type> given_signature;
   protected final List<Type> allowed_signature;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public SignatureTypeMismatchException
   (
      final Origin call_origin,
      final List<Type> given_signature,
      final List<Type> allowed_signature
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.given_signature = given_signature;
      this.allowed_signature = allowed_signature;
   }


   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Given signature:");
      sb.append(System.lineSeparator());

      for (final Type t: given_signature)
      {
         sb.append("- ");
         sb.append(t.toString());
         sb.append(System.lineSeparator());
      }

      sb.append(System.lineSeparator());

      sb.append("Expected signature:");
      sb.append(System.lineSeparator());

      for (final Type t: allowed_signature)
      {
         sb.append("- ");
         sb.append(t.toString());
         sb.append(System.lineSeparator());
      }

      return sb.toString();
   }
}
