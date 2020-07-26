package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Operation extends Computation
{
   /* Math operations *********************************************************/
   public static final String DIVIDE = "divide";
   public static final String MINUS = "minus";
   public static final String PLUS = "plus";
   public static final String POWER = "power";
   public static final String RAND = "rand";
   public static final String TIMES = "times";

   /* Logic operations ********************************************************/
   public static final String AND = "and";
   public static final String OR = "or";
   public static final String NOT = "not";
   public static final String IMPLIES = "implies";

   /* Comparison operations ***************************************************/
   public static final String LESS_THAN = "less_than";
   public static final String LESS_EQUAL_THAN = "less_equal_than";
   public static final String EQUALS = "equals";
   public static final String GREATER_EQUAL_THAN = "greater_equal_than";
   public static final String GREATER_THAN = "greather_than";

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
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(AND, Type.BOOLEAN, param_a, param_b);
   }

   public static Operation or
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(OR, Type.BOOLEAN, param_a, param_b);
   }

   public static Operation not (final Computation param_a)
   {
      return new Operation(NOT, Type.BOOLEAN, param_a, null);
   }

   public static Operation implies
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(IMPLIES, Type.BOOLEAN, param_a, param_b);
   }

   /* Comparison operations ***************************************************/
   public static Operation less_than
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(LESS_THAN, Type.BOOLEAN, param_a, param_b);
   }

   public static Operation less_equal_than
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(LESS_EQUAL_THAN, Type.BOOLEAN, param_a, param_b);
   }
   public static Operation equals
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(EQUALS, Type.BOOLEAN, param_a, param_b);
   }

   public static Operation greater_than
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(GREATER_THAN, Type.BOOLEAN, param_a, param_b);
   }

   public static Operation greater_equal_than
   (
      final Computation param_a,
      final Computation param_b
   )
   {
      return new Operation(GREATER_EQUAL_THAN, Type.BOOLEAN, param_a, param_b);
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
}
