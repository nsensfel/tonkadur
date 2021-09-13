package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.Sequence;

import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class UserInstruction extends GenericInstruction
{
   protected static final Map<String, Sequence> DEFINITIONS;

   static
   {
      DEFINITIONS = new HashMap<String, Sequence>();
   }

   public static Collection<String> get_aliases ()
   {
      // Will be added individually later.
      return new ArrayList<String>();
   }

   public static void register (final Sequence sequence)
   {
      GenericInstruction.register(sequence.get_name(), UserInstruction.class);

      DEFINITIONS.put(sequence.get_name(), sequence);
   }

   public static Collection<Sequence> get_all_definitions ()
   {
      return DEFINITIONS.values();
   }

   public static Instruction build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Sequence definition;

      definition = DEFINITIONS.get(alias);

      if (definition == null)
      {
         System.err.println(origin.toString());
         System.err.println
         (
            "[F] Programming error: the User Instruction '"
            + alias
            + "' was registered without an associated Sequence."
         );
         System.exit(-1);
      }

      definition.propagate_expected_types_and_assert_can_take_parameters
      (
         origin,
         call_parameters
      );

      return new UserInstruction(origin, definition, call_parameters);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Sequence definition;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected UserInstruction
   (
      final Origin origin,
      final Sequence definition,
      final List<Computation> parameters
   )
   {
      super(origin);

      this.definition = definition;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Accessors ************************************************************/
   public Sequence get_definition ()
   {
      return definition;
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

      sb.append("(");
      sb.append(definition.get_name());

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
