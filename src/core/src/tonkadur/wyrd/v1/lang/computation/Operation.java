package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class Operation extends Computation
{
   /* Math operations *********************************************************/
   public static final String DIVIDE = "divide";
   public static final String MINUS = "minus";
   public static final String MODULO = "modulo";
   public static final String PLUS = "plus";
   public static final String POWER = "power";
   public static final String RAND = "rand";
   public static final String TIMES = "times";

   /* Logic operations ********************************************************/
   public static final String AND = "and";
   public static final String NOT = "not";

   /* Comparison operations ***************************************************/
   public static final String LESS_THAN = "less_than";
   public static final String EQUALS = "equals";

   public static Operation divide
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(DIVIDE, param_a.get_type(), param_a, param_b);
   }

   public static Operation minus
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(MINUS, param_a.get_type(), param_a, param_b);
   }

   public static Operation modulo
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(MODULO, param_a.get_type(), param_a, param_b);
   }

   public static Operation plus
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(PLUS, param_a.get_type(), param_a, param_b);
   }

   public static Operation times
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(TIMES, param_a.get_type(), param_a, param_b);
   }

   public static Operation power
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(POWER, param_a.get_type(), param_a, param_b);
   }

   public static Operation rand
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(RAND, Type.INT, param_a, param_b);
   }

   /* Logic operations ********************************************************/
   public static Operation and
   (
      final Computation a,
      final Computation b
   )
   {
      return new Operation(AND, Type.BOOLEAN, a, b);
   }

   public static Operation or
   (
      final Computation a,
      final Computation b
   )
   {
      return not(and(not(a), not(b)));
   }

   public static Operation not (final Computation a)
   {
      return new Operation(NOT, Type.BOOLEAN, a, null);
   }

   public static Operation implies
   (
      final Computation a,
      final Computation b
   )
   {
      return not(and(a, not(b)));
   }

   /* Comparison operations ***************************************************/
   public static Operation less_than
   (
      final Computation a,
      final Computation b
   )
   {
      return new Operation(LESS_THAN, Type.BOOLEAN, a, b);
   }

   public static Operation less_equal_than
   (
      final Computation a,
      final Computation b
   )
   {
      return or(less_than(a, b), equals(a, b));
   }
   public static Operation equals
   (
      final Computation a,
      final Computation b
   )
   {
      return new Operation(EQUALS, Type.BOOLEAN, a, b);
   }

   public static Operation greater_than
   (
      final Computation a,
      final Computation b
   )
   {
      return less_than(b, a);
   }

   public static Operation greater_equal_than
   (
      final Computation a,
      final Computation b
   )
   {
      return or(greater_than(a, b), Operation.equals(a, b));
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String operator;
   protected final Computation param_a;
   protected final Computation param_b;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Operation
   (
      final String operator,
      final Type result_type,
      final Computation param_a,
      final Computation param_b
   )
   {
      super(result_type);

      this.operator = operator;
      this.param_a = param_a;
      this.param_b = param_b;
   }

   /**** Accessors ************************************************************/
   public Computation get_first_parameter ()
   {
      return param_a;
   }

   public Computation get_secomd_parameter ()
   {
      return param_b;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_operation(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(");
      sb.append(operator);
      sb.append(" ");
      sb.append(param_a.toString());

      if (param_b != null)
      {
         sb.append(" ");
         sb.append(param_b.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
