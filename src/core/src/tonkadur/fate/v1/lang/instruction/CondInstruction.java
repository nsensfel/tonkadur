package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.functional.Cons;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class CondInstruction extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Cons<Computation, Instruction>> branches;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CondInstruction
   (
      final Origin origin,
      final List<Cons<Computation, Instruction>> branches
   )
   {
      super(origin);

      this.branches = branches;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static CondInstruction build
   (
      final Origin origin,
      final List<Cons<Computation, Instruction>> branches
   )
   throws ParsingError
   {
      for (final Cons<Computation, Instruction> branch: branches)
      {
         branch.get_car().expect_non_string();
         RecurrentChecks.assert_can_be_used_as(branch.get_car(), Type.BOOL);
      }

      return new CondInstruction(origin, branches);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_cond_instruction(this);
   }

   public List<Cons<Computation, Instruction>> get_branches ()
   {
      return branches;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(CondInstruction");
      sb.append(System.lineSeparator());

      for (final Cons<Computation, Instruction> branch: branches)
      {
         sb.append(System.lineSeparator());
         sb.append("if:");
         sb.append(branch.get_car().toString());

         sb.append(System.lineSeparator());
         sb.append("then:");
         sb.append(branch.get_cdr().toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
