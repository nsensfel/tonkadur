package tonkadur.error;

import tonkadur.RuntimeParameters;

public class ErrorManager
{
   /* Utility class */
   private ErrorManager () {};

   public static <CustomError extends Error>
   void handle (final CustomError error)
   throws CustomError
   {
      final ErrorCategory error_category;
      ErrorLevel error_level;

      error_category = error.get_error_category();

      if (RuntimeParameters.error_is_disabled(error_category))
      {
         return;
      }

      error_level = error.get_error_level();

      if
      (
         (error_level == ErrorLevel.WARNING)
         && RuntimeParameters.warnings_are_errors()
      )
      {
         error_level = ErrorLevel.ERROR;
      }

      if
      (
         (error_level == ErrorLevel.ERROR)
         && RuntimeParameters.error_is_tolerated(error_category)
      )
      {
         error_level = ErrorLevel.WARNING;
      }

      switch (error_level)
      {
         case WARNING:
            System.err.println(error.toString());
            break;

         case ERROR:
            throw error;

         case FATAL:
            System.err.println(error.toString());
            System.exit(-1);
      }
   }
}
