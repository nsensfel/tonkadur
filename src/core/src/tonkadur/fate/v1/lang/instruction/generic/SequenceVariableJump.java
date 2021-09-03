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

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class SequenceVariableJump extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("jump");
      aliases.add("jump_to");
      aliases.add("jump_to_sequence");
      aliases.add("jump_to_procedure");
      aliases.add("jump_to_seq");
      aliases.add("jump_to_proc");
      aliases.add("continue_as");
      aliases.add("continue_as_sequence");
      aliases.add("continue_as_procedure");
      aliases.add("continue_as_seq");
      aliases.add("continue_as_proc");
      aliases.add("continue_to");
      aliases.add("continue_to_sequence");
      aliases.add("continue_to_procedure");
      aliases.add("continue_to_seq");
      aliases.add("continue_to_proc");
      aliases.add("continue_with");
      aliases.add("continue_with_sequence");
      aliases.add("continue_with_procedure");
      aliases.add("continue_with_seq");
      aliases.add("continue_with_proc");
      aliases.add("go_to");
      aliases.add("go_to_sequence");
      aliases.add("go_to_procedure");
      aliases.add("go_to_seq");
      aliases.add("go_to_proc");

      aliases.add("jumpto");
      aliases.add("jumptosequence");
      aliases.add("jumptoprocedure");
      aliases.add("jumptoseq");
      aliases.add("jumptoproc");
      aliases.add("continueas");
      aliases.add("continueassequence");
      aliases.add("continueasprocedure");
      aliases.add("continueasseq");
      aliases.add("continueasproc");
      aliases.add("continueto");
      aliases.add("continuetosequence");
      aliases.add("continuetoprocedure");
      aliases.add("continuetoseq");
      aliases.add("continuetoproc");
      aliases.add("continuewith");
      aliases.add("continuewithsequence");
      aliases.add("continuewithprocedure");
      aliases.add("continuewithseq");
      aliases.add("continuewithproc");
      aliases.add("goto");
      aliases.add("gotosequence");
      aliases.add("gotoprocedure");
      aliases.add("gotoseq");
      aliases.add("gotoproc");

      aliases.add("jumpTo");
      aliases.add("jumpToSequence");
      aliases.add("jumpToProcedure");
      aliases.add("jumpToSeq");
      aliases.add("jumpToProc");
      aliases.add("continueAs");
      aliases.add("continueAsSequence");
      aliases.add("continueAsProcedure");
      aliases.add("continueAsSeq");
      aliases.add("continueAsProc");
      aliases.add("continueTo");
      aliases.add("continueToSequence");
      aliases.add("continueToProcedure");
      aliases.add("continueToSeq");
      aliases.add("continueToProc");
      aliases.add("continueWith");
      aliases.add("continueWithSequence");
      aliases.add("continueWithProcedure");
      aliases.add("continueWithSeq");
      aliases.add("continueWithProc");
      aliases.add("goTo");
      aliases.add("goToSequence");
      aliases.add("goToProcedure");
      aliases.add("goToSeq");
      aliases.add("goToProc");

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
      final Computation sequence;
      final List<Computation> parameters;

      if (call_parameters.size() < 1)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      sequence = call_parameters.get(0);
      parameters = call_parameters.subList(1, call_parameters.size());

      RecurrentChecks.propagate_expected_types_and_assert_is_sequence
      (
         sequence,
         parameters
      );

      return new SequenceVariableJump(origin, sequence, parameters);
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
   protected SequenceVariableJump
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

      sb.append("(SequenceVariableJump ");
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
