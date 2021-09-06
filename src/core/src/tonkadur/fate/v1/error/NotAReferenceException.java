package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;

public class NotAReferenceException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation computation;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public NotAReferenceException
   (
      final Computation computation
   )
   {
      super
      (
         ErrorLevel.WARNING,
         ErrorCategory.INVALID_USE,
         computation.get_origin()
      );

      this.computation = computation;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("The computation ");
      sb.append(computation.toString());
      sb.append(" is being used as a reference, but isn't one.");
      sb.append(" The instruction using it will not be able to modify the");
      sb.append(" memory you thought was targeted by this computation.");

      return sb.toString();
   }
}
