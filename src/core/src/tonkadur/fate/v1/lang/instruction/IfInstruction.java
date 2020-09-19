package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class IfInstruction extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final List<Instruction> if_true;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfInstruction
   (
      final Origin origin,
      final Computation condition,
      final List<Instruction> if_true
   )
   {
      super(origin);

      this.condition = condition;
      this.if_true = if_true;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfInstruction build
   (
      final Origin origin,
      final Computation condition,
      final List<Instruction> if_true
   )
   throws ParsingError
   {
      RecurrentChecks.assert_can_be_used_as(condition, Type.BOOL);

      return new IfInstruction(origin, condition, if_true);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_if_instruction(this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public List<Instruction> get_if_true ()
   {
      return if_true;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IfInstruction");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      for (final Instruction instr: if_true)
      {
         sb.append(System.lineSeparator());
         sb.append(instr.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
