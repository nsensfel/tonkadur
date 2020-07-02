package tonkadur.fate.v1.error;

import tonkadur.fate.v1.lang.Type;

public class TypeAlreadyDeclaredException extends InputException
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type original_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public TypeAlreadyDeclaredException (final Type original_type)
   {
      this.original_type = original_type;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(super.toString());
      sb.append(" Type '");
      sb.append(original_type.get_name());
      sb.append("' already declared in ");
      sb.append(original_type.get_source().toString());
      sb.append(".");

      return sb.toString();
   }
}
