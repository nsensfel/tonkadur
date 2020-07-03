package tonkadur.error;

public abstract class Error extends Throwable
{
   /***************************************************************************/
   /**** STATIC MEMBERS *******************************************************/
   /***************************************************************************/
   private static final long serialVersionUID = 42L;

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ErrorLevel error_level;
   protected final ErrorCategory error_category;

   /***************************************************************************/
   /**** PRIVATE **************************************************************/
   /***************************************************************************/
   protected Error
   (
      final ErrorLevel error_level,
      final ErrorCategory error_category
   )
   {
      this.error_level = error_level;
      this.error_category = error_category;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public ErrorLevel get_error_level ()
   {
      return error_level;
   }

   public ErrorCategory get_error_category ()
   {
      return error_category;
   }
}
