package tonkadur;

import java.util.Collection;
import java.util.HashSet;

import tonkadur.error.ErrorCategory;

public class RuntimeParameters
{
   protected static final Collection<ErrorCategory> disabled_errors;
   protected static final Collection<ErrorCategory> tolerated_errors;
   protected static boolean consider_warnings_as_errors;

   static
   {
      disabled_errors = new HashSet<ErrorCategory>();
      tolerated_errors = new HashSet<ErrorCategory>();
      consider_warnings_as_errors = false;
   }

   public static boolean error_is_disabled (final ErrorCategory category)
   {
      return disabled_errors.contains(category);
   }

   public static boolean error_is_tolerated (final ErrorCategory category)
   {
      return tolerated_errors.contains(category);
   }

   public static boolean warnings_are_errors ()
   {
      return consider_warnings_as_errors;
   }
}
