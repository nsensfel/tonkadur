package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class SetValue extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("set");
      aliases.add("set_value");
      aliases.add("set_value_of");
      aliases.add("setvalue");
      aliases.add("setvalueof");
      aliases.add("setValue");
      aliases.add("setValueOf");

      return aliases;
   }

   public static Instruction build
   (
      final Origin origin,
      final Computation reference,
      final Computation value
   )
   throws Throwable
   {
      reference.expect_non_string();

      RecurrentChecks.handle_expected_type_propagation
      (
         value,
         reference.get_type()
      );

      RecurrentChecks.assert_can_be_used_as(value, reference);

      reference.use_as_reference();

      return new SetValue(origin, value, reference);
   }

   public static Instruction build
   (
      final Origin origin,
      final String _alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      if (call_parameters.size() != 2)
      {
         // TODO: Error.
         System.err.print
         (
            "[E] Wrong number of arguments at "
            + origin.toString()
         );

         return null;
      }

      return build(origin, call_parameters.get(0), call_parameters.get(1));
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation element;
   protected final Computation value_reference;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SetValue
   (
      final Origin origin,
      final Computation element,
      final Computation value_reference
   )
   {
      super(origin);

      this.value_reference = value_reference;
      this.element = element;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_value ()
   {
      return element;
   }

   public Computation get_reference ()
   {
      return value_reference;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(SetValue");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("element:");
      sb.append(System.lineSeparator());
      sb.append(element.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("value_reference:");
      sb.append(System.lineSeparator());
      sb.append(value_reference.toString());

      sb.append(")");

      return sb.toString();
   }
}
