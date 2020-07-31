package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class RelativeRef extends Ref
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation member;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RelativeRef
   (
      final Ref parent,
      final Computation member,
      final Type target_type
   )
   {
      super(parent, target_type);

      this.member = member;
   }

   /**** Accessors ************************************************************/
   public Computation get_member ()
   {
      return member;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(RelativeRef ");
      sb.append(address.toString());
      sb.append(".");
      sb.append(member.toString());
      sb.append(")");

      return sb.toString();
   }
}
