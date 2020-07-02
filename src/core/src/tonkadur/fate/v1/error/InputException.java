package tonkadur.fate.v1.error;

abstract class InputException extends Throwable
{
   /***************************************************************************/
   /**** STATIC MEMBERS *******************************************************/
   /***************************************************************************/
   private static final long serialVersionUID = 42L;

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected String filename = "";
   protected int line = -1;
   protected int column = -1;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public InputException set_location
   (
      final String filename,
      final int line,
      final int column
   )
   {
      this.filename = filename;
      this.line = line;
      this.column = column;

      return this;
   }

   public String get_location ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(filename);
      sb.append(":");
      sb.append(line);
      sb.append(",");
      sb.append(column);

      return sb.toString();
   }

   @Override
   public String toString ()
   {
      return get_location();
   }
}
