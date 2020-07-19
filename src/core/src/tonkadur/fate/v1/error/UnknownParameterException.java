package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.TypedEntryList;

public class UnknownParameterException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final TypedEntryList available_parameters;
   protected final String parameter_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public UnknownParameterException
   (
      final Origin origin,
      final String parameter_name,
      final TypedEntryList available_parameters
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.UNKNOWN,
         origin
      );

      this.parameter_name = parameter_name;
      this.available_parameters = available_parameters;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Unknown parameter '");
      sb.append(parameter_name);
      sb.append("'. ");
      sb.append(System.lineSeparator());
      sb.append("Available parameters:'");

      for
      (
         final TypedEntryList.TypedEntry param:
            available_parameters.get_entries()
      )
      {
         sb.append(System.lineSeparator());
         sb.append("- ");
         sb.append(param.get_name());
      }

      return sb.toString();
   }
}
