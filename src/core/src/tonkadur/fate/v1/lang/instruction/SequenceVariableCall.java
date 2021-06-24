package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.SequenceType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class SequenceVariableCall extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> parameters;
   protected final Computation sequence;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SequenceVariableCall
   (
      final Origin origin,
      final Computation sequence,
      final List<Computation> parameters
   )
   {
      super(origin);

      this.sequence = sequence;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static SequenceVariableCall build
   (
      final Origin origin,
      final Computation sequence,
      final List<Computation> parameters
   )
   throws ParsingError
   {
      final List<Type> signature;

      ((SequenceType) sequence.get_type()).propose_signature_from_parameters
      (
         parameters
      );

      if (!sequence.get_type().get_act_as_type().equals(SequenceType.ARCHETYPE))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               sequence.get_type(),
               Collections.singleton(SequenceType.ARCHETYPE)
            )
         );
      }

      RecurrentChecks.assert_computations_matches_signature
      (
         origin,
         parameters,
         ((SequenceType) sequence.get_type()).get_signature()
      );

      return new SequenceVariableCall(origin, sequence, parameters);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_sequence_variable_call(this);
   }

   public Computation get_sequence ()
   {
      return sequence;
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

      sb.append("(SequenceVariableCall ");
      sb.append(sequence);

      for (final Computation c: parameters)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
