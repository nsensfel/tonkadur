package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class IfElseValue extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Computation if_true;
   protected final Computation if_false;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfElseValue
   (
      final Origin origin,
      final Type return_type,
      final Computation condition,
      final Computation if_true,
      final Computation if_false
   )
   {
      super(origin, return_type);

      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfElseValue build
   (
      final Origin origin,
      final Computation condition,
      final Computation if_true,
      final Computation if_false
   )
   throws ParsingError
   {
      final Type type;

      RecurrentChecks.assert_can_be_used_as(condition, Type.BOOL);

      type =
         RecurrentChecks.assert_can_be_used_as(if_false, if_true.get_type());

      return new IfElseValue(origin, type, condition, if_true, if_false);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_if_else_value(this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public Computation get_if_true ()
   {
      return if_true;
   }

   public Computation get_if_false ()
   {
      return if_false;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IfElseValue");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Condition:");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("If true:");
      sb.append(System.lineSeparator());
      sb.append(if_true.toString());

      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("If false:");
      sb.append(System.lineSeparator());
      sb.append(if_false.toString());

      sb.append(")");

      return sb.toString();
   }
}
