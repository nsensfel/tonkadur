package tonkadur.wyrd.v1.lang.type;

public class Type
{
   public static final Type BOOLEAN;
   public static final Type FLOAT;
   public static final Type INT;
   public static final Type STRING;

   static
   {
      BOOLEAN = new Type("boolean");
      FLOAT = new Type("float");
      INT = new Type("int");
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
}
