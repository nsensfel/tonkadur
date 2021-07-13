package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

//import tonkadur.fate.v1.error.ConflictingTypeException;
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
   protected static final Operation ARCHETYPE;

   static
   {
      final List<Computation> operands;
      List<String> aliases;

      operands = new ArrayList<Computation>();

      operands.add
      (
         Constant.build_boolean
         (
            Origin.BASE_LANGUAGE,
            false
         )
      );

      operands.add(operands.get(0));

      ARCHETYPE =
         new Operation
         (
            Origin.BASE_LANGUAGE,
            Type.BOOL,
            Operator.AND,
            operands
         );

      /**** PLUS **************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("+");
      aliases.add("plus");

      try
      {
         ARCHETYPE.register(aliases, Operator.PLUS);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** MINUS *************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("-");
      aliases.add("minus");

      try
      {
         ARCHETYPE.register(aliases, Operator.MINUS);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** TIMES *************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("*");
      aliases.add("times");

      try
      {
         ARCHETYPE.register(aliases, Operator.TIMES);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** DIVIDE ************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("/");
      aliases.add("div");
      aliases.add("divide");

      try
      {
         ARCHETYPE.register(aliases, Operator.DIVIDE);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** POWER *************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("**");
      aliases.add("^");
      aliases.add("pow");
      aliases.add("power");

      try
      {
         ARCHETYPE.register(aliases, Operator.POWER);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** MODULO ************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("%");
      aliases.add("mod");
      aliases.add("modulo");

      try
      {
         ARCHETYPE.register(aliases, Operator.MODULO);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** MIN ***************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("min");
      aliases.add("minimum");

      try
      {
         ARCHETYPE.register(aliases, Operator.MIN);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** MAX ***************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("max");
      aliases.add("maximum");

      try
      {
         ARCHETYPE.register(aliases, Operator.MAX);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** CLAMP *************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("clamp");

      try
      {
         ARCHETYPE.register(aliases, Operator.CLAMP);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** ABS ***************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("abs");
      aliases.add("absolute");

      try
      {
         ARCHETYPE.register(aliases, Operator.ABS);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** RANDOM ************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("rnd");
      aliases.add("rand");
      aliases.add("random");

      try
      {
         ARCHETYPE.register(aliases, Operator.RANDOM);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** AND ***************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("and");
      aliases.add("/\\");

      try
      {
         ARCHETYPE.register(aliases, Operator.AND);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** OR ****************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("or");
      aliases.add("\\/");

      try
      {
         ARCHETYPE.register(aliases, Operator.OR);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** NOT ***************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("not");
      aliases.add("~");
      aliases.add("!");

      try
      {
         ARCHETYPE.register(aliases, Operator.NOT);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** IMPLIES ***********************************************************/
      aliases = new ArrayList<String>();

      aliases.add("implies");
      aliases.add("=>");
      aliases.add("->");

      try
      {
         ARCHETYPE.register(aliases, Operator.IMPLIES);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** ONE IN ************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("exactlyOneIn");
      aliases.add("exactlyonein");
      aliases.add("exactly_one_in");
      aliases.add("OneIn");
      aliases.add("onein");
      aliases.add("one_in");

      try
      {
         ARCHETYPE.register(aliases, Operator.ONE_IN);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** EQUALS ************************************************************/
      aliases = new ArrayList<String>();

      aliases.add("equals");
      aliases.add("eq");
      aliases.add("=");
      aliases.add("==");

      try
      {
         ARCHETYPE.register(aliases, Operator.EQUALS);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** LOWER THAN ********************************************************/
      aliases = new ArrayList<String>();

      aliases.add("lowerThan");
      aliases.add("lowerthan");
      aliases.add("lower_than");
      aliases.add("lt");
      aliases.add("<");

      try
      {
         ARCHETYPE.register(aliases, Operator.LOWER_THAN);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** LOWER EQUAL THAN **************************************************/
      aliases = new ArrayList<String>();

      aliases.add("lowerEqualThan");
      aliases.add("lowerequalthan");
      aliases.add("lower_equal_than");
      aliases.add("le");
      aliases.add("<=");
      aliases.add("=<");

      try
      {
         ARCHETYPE.register(aliases, Operator.LOWER_EQUAL_THAN);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** GREATER EQUAL THAN **************************************************/
      aliases = new ArrayList<String>();

      aliases.add("greaterEqualThan");
      aliases.add("greaterequalthan");
      aliases.add("greater_equal_than");
      aliases.add("ge");
      aliases.add(">=");

      try
      {
         ARCHETYPE.register(aliases, Operator.GREATER_EQUAL_THAN);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }

      /**** GREATER THAN ******************************************************/
      aliases = new ArrayList<String>();

      aliases.add("greaterThan");
      aliases.add("greaterthan");
      aliases.add("greater_than");
      aliases.add("gt");
      aliases.add(">");

      try
      {
         ARCHETYPE.register(aliases, Operator.GREATER_THAN);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }
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
      super(origin, result_type, "+");

      this.operator = operator;
      this.operands = operands;
   }

   @Override
   public GenericComputation build
   (
      final Origin origin,
      final List<Computation> operands,
      final Object registered_parameter
   )
   throws Throwable
   {
      final Operator operator;
      final Collection<Type> allowed_base_types;
      final int operator_max_arity;
      final int operator_min_arity;
      final int operands_size;
      Type computed_type, previous_computed_type;

      operator = (Operator) registered_parameter;

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
