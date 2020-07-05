package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class InvalidArityException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final int arity;
   protected final int arguments_count;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public InvalidArityException
   (
      final Origin call_origin,
      final int arguments_count,
      final int arity
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.arguments_count = arguments_count;
      this.arity = arity;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.get_context().toString());
      sb.append(error_category.toString());
      sb.append(" This supports a maximum or a minimum of ");
      sb.append(arity);
      sb.append(" parameter(s), but was given ");
      sb.append(arguments_count);
      sb.append(" of them.");

      return sb.toString();
   }
}
