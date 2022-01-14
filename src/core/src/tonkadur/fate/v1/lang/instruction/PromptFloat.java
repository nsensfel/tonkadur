package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class PromptFloat extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final Computation min;
   protected final Computation max;
   protected final Computation label;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PromptFloat
   (
      final Origin origin,
      final Computation target,
      final Computation min,
      final Computation max,
      final Computation label
   )
   {
      super(origin);

      this.target = target;
      this.min = min;
      this.max = max;
      this.label = label;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static PromptFloat build
   (
      final Origin origin,
      final Computation target,
      final Computation min,
      final Computation max,
      final Computation label
   )
   throws ParsingError
   {
      target.expect_non_string();
      min.expect_non_string();
      max.expect_non_string();
      label.expect_string();

      RecurrentChecks.assert_can_be_used_as(min, Type.INT);
      RecurrentChecks.assert_can_be_used_as(max, Type.INT);
      RecurrentChecks.assert_can_be_used_as(label, Type.TEXT);
      RecurrentChecks.assert_can_be_used_as
      (
         target,
         new PointerType(origin, Type.INT, "auto generated")
      );

      return new PromptFloat(origin, target, min, max, label);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_prompt_float(this);
   }

   public Computation get_target ()
   {
      return target;
   }

   public Computation get_max ()
   {
      return max;
   }

   public Computation get_min ()
   {
      return min;
   }

   public Computation get_label ()
   {
      return label;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(PromptFloat ");
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
