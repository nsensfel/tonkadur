package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.computation.Ref;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class SetValue extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Ref reference;
   protected final Computation value;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetValue (final Ref reference, final Computation value)
   {
      this.reference = reference;
      this.value = value;
   }

   /**** Accessors ************************************************************/
   public Ref get_reference ()
   {
      return reference;
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
      sb.append(reference.toString());
      sb.append(" ");
      sb.append(value.toString());
      sb.append(")");

      return sb.toString();
   }
}
