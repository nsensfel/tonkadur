package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class Remove extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Address address;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Remove (final Address address)
   {
      this.address = address;
   }

   /**** Accessors ************************************************************/
   public Address get_address ()
   {
      return address;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_remove(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Remove ");
      sb.append(address.toString());
      sb.append(")");

      return sb.toString();
   }
}
