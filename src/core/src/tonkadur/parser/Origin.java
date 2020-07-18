package tonkadur.parser;

public class Origin
{
   public static final Origin BASE_LANGUAGE;

   static
   {
      BASE_LANGUAGE = new Origin(Context.BASE_LANGUAGE, Location.BASE_LANGUAGE);
   }

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

   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(context.toString());
      sb.append(location.toString());

      return sb.toString();
   }
}
