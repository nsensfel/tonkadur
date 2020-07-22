package tonkadur.wyrd.v1.error;

import tonkadur.error.Error;
import tonkadur.error.ErrorLevel;
import tonkadur.error.ErrorCategory;

public class UnhandledASTElementException extends Error
{
   public UnhandledASTElementException ()
   {
      super(ErrorLevel.FATAL, ErrorCategory.PROGRAMMING_ERROR);
   }
}
