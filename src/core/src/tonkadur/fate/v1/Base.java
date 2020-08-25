package tonkadur.fate.v1;

import tonkadur.fate.v1.error.ErrorCategory;

public class Base
{
   public static void initialize ()
   {
      ErrorCategory.initialize();
   }
}
