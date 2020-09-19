package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class SetValue extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation address;
   protected final Computation value;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetValue (final Computation address, final Computation value)
   {
      this.address = address;
      this.value = value;
   }

   /**** Accessors ************************************************************/
   public Computation get_address ()
   {
      return address;
   }

   public Computation get_value ()
   {
      return value;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_set_value(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(SetValue ");
      sb.append(address.toString());
      sb.append(" ");
      sb.append(value.toString());
      sb.append(")");

      return sb.toString();
   }
}
