package tonkadur.wyrd.v1.lang.type;

public class Type
{
   public static final Type BOOLEAN;
   public static final Type FLOAT;
   public static final Type INT;
   public static final Type RICH_TEXT;
   public static final Type STRING;

   static
   {
      BOOLEAN = new Type("boolean");
      FLOAT = new Type("float");
      INT = new Type("int");
      RICH_TEXT = new Type("rich_text");
      STRING = new Type("string");
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected String name;

   protected Type (final String name)
   {
      this.name = name;
   }

   public String get_name ()
   {
      return name;
   }

   @Override
   public String toString ()
   {
      return name;
   }

   @Override
   public int hashCode ()
   {
      return toString().hashCode();
   }

   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof Type)
      {
         return ((Type) o).toString().equals(toString());
      }

      return false;
   }
}
