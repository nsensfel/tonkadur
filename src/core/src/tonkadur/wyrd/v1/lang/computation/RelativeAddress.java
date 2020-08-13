package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class RelativeAddress extends Address
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation member;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RelativeAddress
   (
      final Address parent,
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

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_relative_address(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(RelativeAddress ");
      sb.append(address.toString());
      sb.append(".");
      sb.append(member.toString());
      sb.append(")");

      return sb.toString();
   }
}
