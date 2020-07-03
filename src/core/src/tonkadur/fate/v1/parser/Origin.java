package tonkadur.fate.v1.parser;

public class Origin
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Context context;
   protected final Location location;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public Origin
   (
      final Context context,
      final Location location
   )
   {
      this.context = context;
      this.location = location;
   }

   public Context get_context ()
   {
      return context;
   }

   public Location get_location ()
   {
      return location;
   }
}
