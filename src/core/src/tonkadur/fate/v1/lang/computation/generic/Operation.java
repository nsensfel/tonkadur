package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

//import tonkadur.fate.v1.error.ConflictingTypeException;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.computation.GenericComputation;
import tonkadur.fate.v1.lang.computation.Operator;
import tonkadur.fate.v1.lang.computation.Constant;

public class Operation extends GenericComputation
{
   protected static final Map<String, Operator> ALIASED_OPERATOR;

   static
   {
      Operator operator;

      ALIASED_OPERATOR = new HashMap<String, Operator>();

      operator = Operator.PLUS;
      ALIASED_OPERATOR.put("+", operator);
      ALIASED_OPERATOR.put("plus", operator);

      operator = Operator.MINUS;
      ALIASED_OPERATOR.put("-", operator);
      ALIASED_OPERATOR.put("minus", operator);

      operator = Operator.TIMES;
      ALIASED_OPERATOR.put("*", operator);
      ALIASED_OPERATOR.put("times", operator);

      operator = Operator.DIVIDE;
      ALIASED_OPERATOR.put("/", operator);
      ALIASED_OPERATOR.put("div", operator);
      ALIASED_OPERATOR.put("divide", operator);

      operator = Operator.POWER;
      ALIASED_OPERATOR.put("**", operator);
      ALIASED_OPERATOR.put("^", operator);
      ALIASED_OPERATOR.put("pow", operator);
      ALIASED_OPERATOR.put("power", operator);

      operator = Operator.MODULO;
      ALIASED_OPERATOR.put("%", operator);
      ALIASED_OPERATOR.put("mod", operator);
      ALIASED_OPERATOR.put("modulo", operator);

      operator = Operator.MIN;
      ALIASED_OPERATOR.put("min", operator);
      ALIASED_OPERATOR.put("minimum", operator);

      operator = Operator.MAX;
      ALIASED_OPERATOR.put("max", operator);
      ALIASED_OPERATOR.put("maximum", operator);

      operator = Operator.CLAMP;
      ALIASED_OPERATOR.put("clamp", operator);

      operator = Operator.ABS;
      ALIASED_OPERATOR.put("abs", operator);
      ALIASED_OPERATOR.put("absolute", operator);

      operator = Operator.RANDOM;
      ALIASED_OPERATOR.put("rnd", operator);
      ALIASED_OPERATOR.put("rand", operator);
      ALIASED_OPERATOR.put("random", operator);

      operator = Operator.AND;
      ALIASED_OPERATOR.put("and", operator);
      ALIASED_OPERATOR.put("/\\", operator);

      operator = Operator.OR;
      ALIASED_OPERATOR.put("or", operator);
      ALIASED_OPERATOR.put("\\/", operator);

      operator = Operator.NOT;
      ALIASED_OPERATOR.put("not", operator);
      ALIASED_OPERATOR.put("~", operator);
      ALIASED_OPERATOR.put("!", operator);

      operator = Operator.IMPLIES;
      ALIASED_OPERATOR.put("implies", operator);
      ALIASED_OPERATOR.put("=>", operator);
      ALIASED_OPERATOR.put("->", operator);

      operator = Operator.ONE_IN;
      ALIASED_OPERATOR.put("exactlyOneIn", operator);
      ALIASED_OPERATOR.put("exactlyonein", operator);
      ALIASED_OPERATOR.put("exactlyOne", operator);
      ALIASED_OPERATOR.put("exactlyone", operator);
      ALIASED_OPERATOR.put("exactly_one_in", operator);
      ALIASED_OPERATOR.put("exactly_one", operator);
      ALIASED_OPERATOR.put("OneIn", operator);
      ALIASED_OPERATOR.put("onein", operator);
      ALIASED_OPERATOR.put("one_in", operator);

      operator = Operator.EQUALS;
      ALIASED_OPERATOR.put("equals", operator);
      ALIASED_OPERATOR.put("eq", operator);
      ALIASED_OPERATOR.put("=", operator);
      ALIASED_OPERATOR.put("==", operator);

      operator = Operator.LOWER_THAN;
      ALIASED_OPERATOR.put("lowerThan", operator);
      ALIASED_OPERATOR.put("lowerthan", operator);
      ALIASED_OPERATOR.put("lower_than", operator);
      ALIASED_OPERATOR.put("lt", operator);
      ALIASED_OPERATOR.put("<", operator);

      operator = Operator.LOWER_EQUAL_THAN;
      ALIASED_OPERATOR.put("lowerEqualThan", operator);
      ALIASED_OPERATOR.put("lowerequalthan", operator);
      ALIASED_OPERATOR.put("lower_equal_than", operator);
      ALIASED_OPERATOR.put("le", operator);
      ALIASED_OPERATOR.put("<=", operator);
      ALIASED_OPERATOR.put("=<", operator);

      operator = Operator.GREATER_THAN;
      ALIASED_OPERATOR.put("greaterEqualThan", operator);
      ALIASED_OPERATOR.put("greaterequalthan", operator);
      ALIASED_OPERATOR.put("greater_equal_than", operator);
      ALIASED_OPERATOR.put("ge", operator);
      ALIASED_OPERATOR.put(">=", operator);

      operator = Operator.GREATER_THAN;
      ALIASED_OPERATOR.put("greaterThan", operator);
      ALIASED_OPERATOR.put("greaterthan", operator);
      ALIASED_OPERATOR.put("greater_than", operator);
      ALIASED_OPERATOR.put("gt", operator);
      ALIASED_OPERATOR.put(">", operator);
   }

   public static Collection<String> get_aliases ()
   {
      return ALIASED_OPERATOR.keySet();
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> operands
   )
   throws Throwable
   {
      return build(origin, ALIASED_OPERATOR.get(alias), operands);
   }

   public static Computation build
   (
      final Origin origin,
      final Operator operator,
      final List<Computation> operands
   )
   throws Throwable
   {
      final Collection<Type> allowed_base_types;
      final int operator_max_arity;
      final int operator_min_arity;
      final int operands_size;
      Type computed_type, previous_computed_type;

      allowed_base_types = operator.get_allowed_base_types();
      operator_max_arity = operator.get_maximum_arity();
      operator_min_arity = operator.get_minimum_arity();
      operands_size = operands.size();

      for (final Computation c: operands)
      {
         c.expect_non_string();
      }

      if
      (
         (operands_size < operator_min_arity)
         ||
         (
            (operator_max_arity != 0)
            && (operator_max_arity < operands_size)
         )
      )
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               origin,
               operands_size,
               operator_min_arity,
               operator_max_arity
            )
         );
      }

      computed_type = operands.get(0).get_type();

      for (final Computation operand: operands)
      {
         final Type operand_type;

         operand_type = operand.get_type();

         if (!allowed_base_types.contains(operand_type.get_act_as_type()))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  operand.get_origin(),
                  operand_type,
                  allowed_base_types
               )
            );
         }

         /*

         if (computed_type.equals(operand_type))
         {
            continue;
         }

         ErrorManager.handle
         (
            new ConflictingTypeException
            (
               operand.get_origin(),
               operand_type,
               computed_type
            )
         );

         */

         if (operand_type.can_be_used_as(computed_type))
         {
            continue;
         }

         previous_computed_type = computed_type;
         computed_type = computed_type.try_merging_with(operand_type);

         if (computed_type != null)
         {
            continue;
         }

         ErrorManager.handle
         (
            new IncompatibleTypeException
            (
               operand.get_origin(),
               operand_type,
               previous_computed_type
            )
         );

         computed_type =
            (Type) previous_computed_type.generate_comparable_to(operand_type);

         if (computed_type.equals(Type.ANY))
         {
            ErrorManager.handle
            (
               new IncomparableTypeException
               (
                  operand.get_origin(),
                  operand_type,
                  previous_computed_type
               )
            );
         }
      }

      computed_type = operator.transform_type(computed_type);

      return new Operation(origin, computed_type, operator, operands);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Operator operator;
   protected final List<Computation> operands;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected Operation
   (
      final Origin origin,
      final Type result_type,
      final Operator operator,
      final List<Computation> operands
   )
   {
      super(origin, result_type);

      this.operator = operator;
      this.operands = operands;
   }

   /**** Accessors ************************************************************/
   public Operator get_operator ()
   {
      return operator;
   }

   public List<Computation> get_operands ()
   {
      return operands;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Operation ");
      sb.append(operator.toString());

      for (final Computation c: operands)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
