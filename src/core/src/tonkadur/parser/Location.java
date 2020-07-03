package tonkadur.parser;

public class Location
{
   /***************************************************************************/
   /**** STATIC MEMBERS *******************************************************/
   /***************************************************************************/
   public static final Location BASE_LANGUAGE;

   static
   {
      BASE_LANGUAGE = new Location(true, "", -1, -1);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final boolean is_base_language;
   protected final String filename;
   protected final int line;
   protected final int column;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected Location
   (
      final boolean is_base_language,
      final String filename,
      final int line,
      final int column
   )
   {
      this.is_base_language = is_base_language;
      this.filename = filename;
      this.line = line;
      this.column = column;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Location
   (
      final String filename,
      final int line,
      final int column
   )
   {
      this.is_base_language = false;
      this.filename = filename;
      this.line = line;
      this.column = column;
   }

   /**** Accessors ************************************************************/
   public String get_filename ()
   {
      return filename;
   }

   public int get_line ()
   {
      return line;
   }

   public int get_column ()
   {
      return line;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
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
   public boolean equals (final Object o)
   {
      if (o instanceof Location)
      {
         final Location b;

         b = (Location) o;

         return
            (
               (filename.equals(b.filename))
               && (line == b.line)
               && (column == b.column)
            );
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return (filename.hashCode() + line + column);
   }
}
