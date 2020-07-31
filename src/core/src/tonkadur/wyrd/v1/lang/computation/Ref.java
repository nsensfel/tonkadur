package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Ref extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation address;
   protected final Type target_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Ref (final Computation address, final Type target_type)
   {
      super(Type.POINTER);

      this.address = address;
      this.target_type = target_type;
   }

   /**** Accessors ************************************************************/
   public Computation get_address ()
   {
      return address;
   }

   public Type get_target_type ()
   {
      return target_type;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Ref ");
      sb.append(address.toString());
      sb.append(")");

      return sb.toString();
   }
}
