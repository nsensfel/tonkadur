package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class SetRandom extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final Computation min, max;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetRandom
   (
      final Computation target,
      final Computation min,
      final Computation max
   )
   {
      this.target = target;
      this.min = min;
      this.max = max;
   }

   /**** Accessors ************************************************************/
   public Computation get_target ()
   {
      return target;
   }

   public Computation get_min ()
   {
      return min;
   }

   public Computation get_max ()
   {
      return max;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_set_random(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(SetRandom ");
      sb.append(target.toString());
      sb.append(" ");
      sb.append(min.toString());
      sb.append(" ");
      sb.append(max.toString());
      sb.append(")");

      return sb.toString();
   }
}
