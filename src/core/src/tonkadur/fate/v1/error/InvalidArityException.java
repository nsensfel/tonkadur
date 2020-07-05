package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class InvalidArityException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final int min_arity;
   protected final int max_arity;
   protected final int arguments_count;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public InvalidArityException
   (
      final Origin call_origin,
      final int arguments_count,
      final int min_arity,
      final int max_arity
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.arguments_count = arguments_count;
      this.min_arity = min_arity;
      this.max_arity = max_arity;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("This supports a minimum of ");
      sb.append(min_arity);

      if (max_arity > 0)
      {
         sb.append(" and a maximum of ");
         sb.append(max_arity);
      }

      sb.append(" parameter(s), but was given ");
      sb.append(arguments_count);
      sb.append(" of them.");

      return sb.toString();
   }
}
