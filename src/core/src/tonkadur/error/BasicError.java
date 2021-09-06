package tonkadur.error;

public class BasicError extends tonkadur.error.Error
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String description;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public BasicError
   (
      final ErrorLevel error_level,
      final ErrorCategory error_category,
      final String description
   )
   {
      super(error_level, error_category);

      this.description = description;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(error_level.toString());
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());
      sb.append(description);

      return sb.toString();
   }
}
