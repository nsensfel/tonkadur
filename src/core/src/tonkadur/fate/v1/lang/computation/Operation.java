package tonkadur.fate.v1.lang.computation;

import java.util.Collection;
import java.util.List;

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

public class Operation extends Computation
{
   protected final Operator operator;
   protected final List<Computation> operands;

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

   public static Operation build
   (
      final Origin origin,
      final Operator operator,
      final List<Computation> operands
   )
   throws
      //ConflictingTypeException,
      IncomparableTypeException,
      IncompatibleTypeException,
      InvalidArityException,
      InvalidTypeException
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

         if (!allowed_base_types.contains(operand_type.get_base_type()))
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
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_operation(this);
   }

   public Operator get_operator ()
   {
      return operator;
   }

   public List<Computation> get_operands ()
   {
      return operands;
   }
}
