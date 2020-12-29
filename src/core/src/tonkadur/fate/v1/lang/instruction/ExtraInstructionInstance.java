package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.ExtraInstruction;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class ExtraInstructionInstance extends Instruction
{
   protected final ExtraInstruction instruction;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ExtraInstructionInstance
   (
      final Origin origin,
      final ExtraInstruction instruction,
      final List<Computation> parameters
   )
   {
      super(origin);

      this.instruction = instruction;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ExtraInstructionInstance build
   (
      final Origin origin,
      final ExtraInstruction instruction,
      final List<Computation> parameters
   )
   throws ParsingError
   {
      RecurrentChecks.assert_computations_matches_signature
      (
         origin,
         parameters,
         instruction.get_signature()
      );

      return new ExtraInstructionInstance(origin, instruction, parameters);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_extra_instruction(this);
   }

   public ExtraInstruction get_instruction_type ()
   {
      return instruction;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(");
      sb.append(instruction.get_name());

      for (final Computation p: parameters)
      {
         sb.append(" ");
         sb.append(p.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
