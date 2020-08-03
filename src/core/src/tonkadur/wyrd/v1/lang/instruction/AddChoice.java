package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class AddChoice extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation label;
   protected final Computation address;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddChoice (final Computation label, final Computation address)
   {
      this.label = label;
      this.address = address;
   }

   /**** Accessors ************************************************************/
   public Computation get_label ()
   {
      return label;
   }

   public Computation get_address ()
   {
      return address;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_add_choice(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(AddChoice ");
      sb.append(label.toString());
      sb.append(" ");
      sb.append(address.toString());
      sb.append(")");

      return sb.toString();
   }
}
