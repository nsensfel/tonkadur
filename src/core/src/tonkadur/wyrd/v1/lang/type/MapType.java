package tonkadur.wyrd.v1.lang.type;

public class MapType extends Type
{
   public static final MapType MAP_TO_BOOLEAN;
   public static final MapType MAP_TO_FLOAT;
   public static final MapType MAP_TO_INT;
   public static final MapType MAP_TO_STRING;

   static
   {
      MAP_TO_BOOLEAN = new MapType(Type.BOOLEAN);
      MAP_TO_FLOAT = new MapType(Type.FLOAT);
      MAP_TO_INT = new MapType(Type.INT);
      MAP_TO_STRING = new MapType(Type.STRING);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type member_type;

   protected MapType (final Type member_type)
   {
      super("(Map String->" + member_type.name + ")");
      this.member_type = member_type;
   }

   public Type get_member_type ()
   {
      return member_type;
   }
}
