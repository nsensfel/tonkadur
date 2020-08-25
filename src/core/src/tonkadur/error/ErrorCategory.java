package tonkadur.error;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ErrorCategory
{
   protected static final Map<String, ErrorCategory> by_name;

   public static final ErrorCategory INVALID_INPUT;
   public static final ErrorCategory PROGRAMMING_ERROR;
   public static final ErrorCategory FILE_ACCESS;

   static
   {
      by_name = new HashMap<String, ErrorCategory>();

      INVALID_INPUT = new ErrorCategory("Invalid Input");
      PROGRAMMING_ERROR = new ErrorCategory("Programming Error");
      FILE_ACCESS = new ErrorCategory("File Access");
   }

   public static ErrorCategory get_error_category (final String name)
   {
      return by_name.get(name);
   }

   public static Collection<String> get_error_categories ()
   {
      return by_name.keySet();
   }

   protected final String name;

   protected ErrorCategory (final String name)
   {
      this.name = name;

      by_name.put(name, this);
   }

   @Override
   public String toString()
   {
      return name;
   }
}
