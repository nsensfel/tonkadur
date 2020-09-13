package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class IfElseInstruction extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Instruction if_true;
   protected final Instruction if_false;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfElseInstruction
   (
      final Origin origin,
      final Computation condition,
      final Instruction if_true,
      final Instruction if_false
   )
   {
      super(origin);

      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfElseInstruction build
   (
      final Origin origin,
      final Computation condition,
      final Instruction if_true,
      final Instruction if_false
   )
   throws ParsingError
   {
      RecurrentChecks.assert_can_be_used_as(condition, Type.BOOL);

      return new IfElseInstruction(origin, condition, if_true, if_false);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_if_else_instruction(this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public Instruction get_if_true ()
   {
      return if_true;
   }

   public Instruction get_if_false ()
   {
      return if_false;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IfElseInstruction");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());
      sb.append(System.lineSeparator());
      sb.append("If true:");
      sb.append(System.lineSeparator());
      sb.append(if_true.toString());
      sb.append(System.lineSeparator());
      sb.append("If false:");
      sb.append(System.lineSeparator());
      sb.append(if_false.toString());

      sb.append(")");

      return sb.toString();
   }
}
