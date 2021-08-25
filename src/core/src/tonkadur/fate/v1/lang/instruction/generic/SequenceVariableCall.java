package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
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

public class SequenceVariableCall extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      // ISSUE: how can the parser distinguish these from (call! <STRING>)
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("call");
      aliases.add("call_sequence");
      aliases.add("call_procedure");
      aliases.add("call_seq");
      aliases.add("call_proc");
      aliases.add("callsequence");
      aliases.add("callprocedure");
      aliases.add("callseq");
      aliases.add("callproc");
      aliases.add("callSequence");
      aliases.add("callProcedure");
      aliases.add("callSeq");
      aliases.add("callProc");
      aliases.add("visit");
      aliases.add("visit_sequence");
      aliases.add("visit_procedure");
      aliases.add("visit_seq");
      aliases.add("visit_proc");
      aliases.add("visitsequence");
      aliases.add("visitprocedure");
      aliases.add("visitseq");
      aliases.add("visitproc");
      aliases.add("visitSequence");
      aliases.add("visitProcedure");
      aliases.add("visitSeq");
      aliases.add("visitProc");

      return aliases;
   }

   public static Instruction build
   (
      final Origin origin,
      final String _alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      // TODO: implement
      // Quite a troublesome one, since we need to handle:
      // * If first argument is a string, this is a reference to a sequence.
      // * In that case, the expected type of each other parameter is to be
      //   determined in the future.
      final Computation sequence = null;
      final List<Computation> parameters = null;
      final List<Type> signature;

      sequence.expect_non_string();

      // TODO: change this system, since we'd rather use the signature to tell
      // the parameters what to expect.
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

      RecurrentChecks.propagate_expected_types_and_assert_computations_matches_signature
      (
         origin,
         parameters,
         ((SequenceType) sequence.get_type()).get_signature()
      );

      return new SequenceVariableCall(origin, sequence, parameters);
   }

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
   /**** Accessors ************************************************************/
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
