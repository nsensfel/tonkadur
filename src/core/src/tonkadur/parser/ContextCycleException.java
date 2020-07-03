package tonkadur.parser;

import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.error.ErrorCategory;
import tonkadur.error.ErrorLevel;

class ContextCycleException extends ParsingError
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
      final Origin origin,
      final Location original_require_location,
      final String filename
   )
   {
      super(ErrorLevel.FATAL, ErrorCategory.INVALID_INPUT, origin);

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
