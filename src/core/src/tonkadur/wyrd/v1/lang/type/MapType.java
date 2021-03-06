package tonkadur.wyrd.v1.lang.type;

public class MapType extends Type
{
   public static final MapType MAP_TO_BOOL;
   public static final MapType MAP_TO_FLOAT;
   public static final MapType MAP_TO_INT;
   public static final MapType MAP_TO_STRING;
   public static final MapType MAP_TO_TEXT;

   static
   {
      MAP_TO_BOOL = new MapType(Type.BOOL);
      MAP_TO_FLOAT = new MapType(Type.FLOAT);
      MAP_TO_INT = new MapType(Type.INT);
      MAP_TO_TEXT = new MapType(Type.TEXT);
      MAP_TO_STRING = new MapType(Type.STRING);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type member_type;

   public MapType (final Type member_type)
   {
      super("(Map String->" + member_type.name + ")");
      this.member_type = member_type;
   }

   public Type get_member_type ()
   {
      return member_type;
   }

   @Override
   public String toString ()
   {
      return name;
   }

   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof MapType)
      {
         return ((MapType) o).member_type.equals(member_type);
      }

      return false;
   }
}
