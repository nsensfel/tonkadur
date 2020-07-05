package tonkadur.fate.v1.error;

import java.util.Collection;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.Type;

public class InvalidTypeException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type given_type;
   protected final Collection<Type> allowed_types;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public InvalidTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Collection<Type> allowed_types
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.given_type = given_type;
      this.allowed_types = allowed_types;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.get_context().toString());
      sb.append(error_category.toString());
      sb.append(" Type '");
      sb.append(given_type.toString());
      sb.append("' ");
      sb.append(" is not useable here. The following base types are allowed:");

      for (final Type allowed_type: allowed_types)
      {
         sb.append(System.lineSeparator());
         sb.append("- ");
         sb.append(allowed_types.toString());
      }

      return sb.toString();
   }
}
