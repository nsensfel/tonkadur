package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

import tonkadur.fate.v1.lang.type.Type;

public class ExtraInstruction extends GenericInstruction
{
   protected static final Map<String, List<Type>> SIGNATURES;

   static
   {
      SIGNATURES = new HashMap<String, List<Type>>();
   }

   public static Collection<String> get_aliases ()
   {
      // These are added individually later.
      return new ArrayList<String>();
   }

   public static void register (final String name, final List<Type> signature)
   {
      GenericInstruction.register(name, ExtraInstruction.class);

      SIGNATURES.put(name, signature);
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ExtraInstruction
   (
      final Origin origin,
      final String instruction_name,
      final List<Computation> parameters
   )
   {
      super(origin);

      this.instruction_name = instruction_name;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Instruction build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final List<Type> signature;

      signature = SIGNATURES.get(alias);

      if (signature == null)
      {
         System.err.println(origin.toString());
         System.err.println
         (
            "[F] Programming error: the Extra Instruction '"
            + alias
            + "' was registered without a signature."
         );
         System.exit(-1);
      }

      RecurrentChecks.propagate_expected_types_and_assert_computations_matches_signature
      (
         origin,
         call_parameters,
         signature
      );

      return new ExtraInstruction(origin, alias, call_parameters);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String instruction_name;
   protected final List<Computation> parameters;

   /**** Accessors ************************************************************/
   public String get_instruction_name ()
   {
      return instruction_name;
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
      sb.append(get_instruction_name());

      for (final Computation p: get_parameters())
      {
         sb.append(" ");
         sb.append(p.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
