package tonkadur.parser;

import tonkadur.error.ErrorCategory;
import tonkadur.error.ErrorLevel;

public class BasicParsingError extends ParsingError
{
   protected final String description;

   public BasicParsingError
   (
      final ErrorLevel error_level,
      final ErrorCategory error_category,
      final Origin origin,
      final String description
   )
   {
      super(error_level, error_category, origin);

      this.description = description;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());
      sb.append(description);

      return sb.toString();
   }
}
