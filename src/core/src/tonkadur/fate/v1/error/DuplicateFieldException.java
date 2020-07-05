package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

public class DuplicateFieldException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Origin previous_definition;
   protected final String name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public DuplicateFieldException
   (
      final Origin new_origin,
      final Origin previous_definition,
      final String name
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.INVALID_USE,
         new_origin
      );

      this.previous_definition = previous_definition;
      this.name = name;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Duplicate field '");
      sb.append(name);
      sb.append("'. It was previously defined at ");
      sb.append(previous_definition.get_location().toString());
      sb.append(".");

      return sb.toString();
   }
}
