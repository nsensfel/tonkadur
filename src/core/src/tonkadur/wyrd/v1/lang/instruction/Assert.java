package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class Assert extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Computation message;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Assert (final Computation condition, final Computation message)
   {
      this.condition = condition;
      this.message = message;
   }

   /**** Accessors ************************************************************/
   public Computation get_condition ()
   {
      return condition;
   }

   public Computation get_message ()
   {
      return message;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_assert(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Assert ");
      sb.append(condition.toString());
      sb.append(" ");
      sb.append(message.toString());
      sb.append(")");

      return sb.toString();
   }
}
