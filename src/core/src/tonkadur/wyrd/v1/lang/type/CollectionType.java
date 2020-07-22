package tonkadur.wyrd.v1.lang.type;

public class CollectionType extends Type
{
   public static final CollectionType BOOLEAN_BAG;
   public static final CollectionType FLOAT_BAG;
   public static final CollectionType INT_BAG;
   public static final CollectionType STRING_BAG;

   public static final CollectionType BOOLEAN_SET;
   public static final CollectionType FLOAT_SET;
   public static final CollectionType INT_SET;
   public static final CollectionType STRING_SET;

   static
   {
      BOOLEAN_BAG = new CollectionType(Type.BOOLEAN, true);
      FLOAT_BAG = new CollectionType(Type.FLOAT, true);
      INT_BAG = new CollectionType(Type.INT, true);
      STRING_BAG = new CollectionType(Type.STRING, true);

      BOOLEAN_SET = new CollectionType(Type.BOOLEAN, false);
      FLOAT_SET = new CollectionType(Type.FLOAT, false);
      INT_SET = new CollectionType(Type.INT, false);
      STRING_SET = new CollectionType(Type.STRING, false);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type member_type;
   protected final boolean is_bag;

   protected CollectionType (final Type member_type, final boolean is_bag)
   {
      super("(" + (is_bag ? "bag " : "set ") + member_type.name + ")");
      this.member_type = member_type;
      this.is_bag = is_bag;
   }

   public Type get_member_type ()
   {
      return member_type;
   }

   public boolean is_bag ()
   {
      return is_bag;
   }
}
