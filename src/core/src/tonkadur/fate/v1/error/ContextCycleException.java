package tonkadur.fate.v1.error;

import tonkadur.fate.v1.parser.Location;
import tonkadur.fate.v1.parser.Origin;

public class ContextCycleException extends InputException
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   /*
    * Using a Location instead of an Origin here, because the file refers to
    * something in 'origin' anyway.
    */
   protected final Location original_require_location;
   protected final String filename;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public ContextCycleException
   (
      final Location original_require_location,
      final String filename
   )
   {
      this.original_require_location = original_require_location;
      this.filename = filename;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.get_context().toString());
      sb.append("Cyclic dependency for file '");
      sb.append(filename);
      sb.append("' required at ");
      sb.append(origin.get_location().toString());
      sb.append(" when it was already required at ");
      sb.append(original_require_location.toString());
      sb.append(".");

      return sb.toString();
   }
}
