package tonkadur.fate.v1.error;

import tonkadur.fate.v1.parser.Context;
import tonkadur.fate.v1.parser.Origin;

abstract class InputException extends Throwable
{
   /***************************************************************************/
   /**** STATIC MEMBERS *******************************************************/
   /***************************************************************************/
   private static final long serialVersionUID = 42L;

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected Origin origin;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public void set_origin (final Origin origin)
   {
      this.origin = origin;
   }

   public void set_origin
   (
      final Context context,
      final int line,
      final int column
   )
   {
      origin = context.get_origin_at(line, column);
   }

   public Origin get_origin ()
   {
      return origin;
   }
}
