package tonkadur.fate.v1.error;

import java.util.Collection;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

public class InvalidTypeException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;
   protected final Type given_type;
   protected final Collection<Type> allowed_types;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public InvalidTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Collection<Type> allowed_types
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.given_type = given_type;
      this.allowed_types = allowed_types;
      this.name = null;
   }

   public InvalidTypeException
   (
      final Origin call_origin,
      final Type given_type,
      final Collection<Type> allowed_types,
      final String name
   )
   {
      super
      (
         ErrorLevel.FATAL,
         ErrorCategory.INVALID_USE,
         call_origin
      );

      this.given_type = given_type;
      this.allowed_types = allowed_types;
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

      if (name != null)
      {
         sb.append(name);
         sb.append(" has type '");
         sb.append(given_type.toString());
         sb.append("', which");
      }
      else
      {
         sb.append("Type '");
         sb.append(given_type.toString());
         sb.append("'");
      }

      sb.append(" is not useable here. The following base types are allowed:");

      for (final Type allowed_type: allowed_types)
      {
         sb.append(System.lineSeparator());
         sb.append("- ");
         sb.append(allowed_type.toString());
      }

      return sb.toString();
   }
}
