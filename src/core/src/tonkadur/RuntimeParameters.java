package tonkadur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import tonkadur.error.ErrorCategory;

public class RuntimeParameters
{
   protected static final List<String> include_directories;
   protected static final Collection<ErrorCategory> disabled_errors;
   protected static final Collection<ErrorCategory> tolerated_errors;
   protected static boolean consider_warnings_as_errors;

   static
   {
      include_directories = new ArrayList<String>();
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

   public static List<String> get_include_directories ()
   {
      return include_directories;
   }

   public static void add_include_directory (final String name)
   {
      include_directories.add(name);
   }
}
