package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.Type;

public class TypeAlreadyDeclaredException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type original_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public TypeAlreadyDeclaredException
   (
      final Origin origin,
      final Type original_type
   )
   {
      super(ErrorLevel.WARNING, ErrorCategory.DUPLICATE_DECLARATION, origin);
      this.original_type = original_type;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.get_context().toString());
      sb.append("Declaration for type '");
      sb.append(original_type.get_name());
      sb.append("' at ");
      sb.append(origin.get_location().toString());
      sb.append(" when it was already declared at ");
      sb.append(original_type.get_origin().get_location().toString());
      sb.append(".");

      return sb.toString();
   }
}
