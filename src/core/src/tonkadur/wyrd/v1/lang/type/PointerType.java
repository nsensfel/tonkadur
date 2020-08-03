package tonkadur.wyrd.v1.lang.type;

public class PointerType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type target_type;

   public PointerType (final Type target_type)
   {
      super("(PointerTo " + target_type.name + ")");
      this.target_type = target_type;
   }

   public Type get_target_type ()
   {
      return target_type;
   }

   @Override
   public String toString ()
   {
      return name;
   }
}
