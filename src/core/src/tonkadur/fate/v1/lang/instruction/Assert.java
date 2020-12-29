package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.TextNode;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.type.Type;

public class Assert extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final TextNode message;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Assert
   (
      final Origin origin,
      final Computation condition,
      final TextNode message
   )
   {
      super(origin);

      this.condition = condition;
      this.message = message;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Assert build
   (
      final Origin origin,
      final Computation condition,
      final TextNode message
   )
   throws ParsingError
   {
      RecurrentChecks.assert_can_be_used_as(condition, Type.BOOL);

      return new Assert(origin, condition, message);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_assert(this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public TextNode get_message ()
   {
      return message;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Assert");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      sb.append(")");

      return sb.toString();
   }
}
