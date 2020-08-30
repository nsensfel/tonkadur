package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class PromptString extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Address target;
   protected final Computation min, max, label;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public PromptString
   (
      final Address target,
      final Computation min,
      final Computation max,
      final Computation label
   )
   {
      this.target = target;
      this.min = min;
      this.max = max;
      this.label = label;
   }

   /**** Accessors ************************************************************/
   public Address get_target()
   {
      return target;
   }

   public Computation get_min()
   {
      return min;
   }

   public Computation get_max()
   {
      return max;
   }

   public Computation get_label()
   {
      return label;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_prompt_string(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(PromptString ");
      sb.append(target.toString());
      sb.append(" ");
      sb.append(min.toString());
      sb.append(" ");
      sb.append(max.toString());
      sb.append(" ");
      sb.append(label.toString());
      sb.append(")");

      return sb.toString();
   }
}