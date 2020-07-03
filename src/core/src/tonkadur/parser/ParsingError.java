package tonkadur.parser;

import tonkadur.error.Error;
import tonkadur.error.ErrorCategory;
import tonkadur.error.ErrorLevel;

public abstract class ParsingError extends Error
{
   protected final Origin origin;

   protected ParsingError
   (
      final ErrorLevel error_level,
      final ErrorCategory error_category,
      final Origin origin
   )
   {
      super(error_level, error_category);
      this.origin = origin;
   }
}
