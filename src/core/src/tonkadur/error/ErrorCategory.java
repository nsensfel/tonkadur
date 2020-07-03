package tonkadur.error;

public class ErrorCategory
{
   public static final ErrorCategory INVALID_INPUT;
   public static final ErrorCategory PROGRAMMING_ERROR;
   public static final ErrorCategory FILE_ACCESS;

   static
   {
      INVALID_INPUT = new ErrorCategory("Invalid Input");
      PROGRAMMING_ERROR = new ErrorCategory("Programming Error");
      FILE_ACCESS = new ErrorCategory("File Access");
   }

   protected final String name;

   protected ErrorCategory (final String name)
   {
      this.name = name;
   }

   @Override
   public String toString()
   {
      return name;
   }
}
