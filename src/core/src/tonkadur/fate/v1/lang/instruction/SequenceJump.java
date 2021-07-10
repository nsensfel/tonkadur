package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class SequenceJump extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> parameters;
   protected final String sequence_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SequenceJump
   (
      final Origin origin,
      final String sequence_name,
      final List<Computation> parameters
   )
   {
      super(origin);

      this.sequence_name = sequence_name;
      this.parameters = parameters;
   }

   /**** Accessors ************************************************************/
   // TODO: call this at some point.
   public void perform_signature_checks (final List<Type> signature)
   throws ParsingError
   {
      RecurrentChecks.propagate_expected_types
      (
         parameters,
         signature
      );

      RecurrentChecks.assert_computations_matches_signature
      (
         get_origin(),
         parameters,
         signature
      );
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_sequence_jump(this);
   }

   public String get_sequence_name ()
   {
      return sequence_name;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(SequenceJump ");
      sb.append(sequence_name);

      for (final Computation c: parameters)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
