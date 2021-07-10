package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;


import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class SetValue extends Instruction
{
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
   /**** Constructors *********************************************************/
   public static SetValue build
   (
      final Origin origin,
      final Computation element,
      final Computation value_reference
   )
   throws ParsingError
   {
      value_reference.expect_non_string();

      RecurrentChecks.assert_can_be_used_as(element, value_reference);

      return new SetValue(origin, element, value_reference);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_set_value(this);
   }

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
