package tonkadur.parser;

public class Origin
{
   public static final Origin BASE_LANGUAGE;

   static
   {
      BASE_LANGUAGE =
         new Origin
         (
            Context.BASE_LANGUAGE,
            Location.BASE_LANGUAGE,
            "base language/core feature/mandatory inclusion"
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Context context;
   protected final Location location;
   protected final String hint;

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
      this.hint = "";
   }

   public Origin
   (
      final Context context,
      final Location location,
      final String hint
   )
   {
      this.context = context;
      this.location = location;
      this.hint = hint;
   }

   public Context get_context ()
   {
      return context;
   }

   public Location get_location ()
   {
      return location;
   }

   public String get_hint ()
   {
      return hint;
   }

   public Origin with_hint (final String hint)
   {
      return new Origin(context.clone(), location.clone(), hint);
   }

   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(context.toString());
      sb.append(location.toString());

      if (hint.length() > 0)
      {
         sb.append("(hint: ");
         sb.append(get_hint());
         sb.append(")");
      }

      return sb.toString();
   }

   @Override
   public Origin clone ()
   {
      return new Origin(context.clone(), location.clone(), hint);
   }
}
