package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.IfElseComputation;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class OperationCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return tonkadur.fate.v1.lang.computation.generic.Operation.class;
   }

   public OperationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final tonkadur.fate.v1.lang.computation.generic.Operation source;
      final String fate_op_name;
      final List<Computation> operands;

      source =
         (tonkadur.fate.v1.lang.computation.generic.Operation) computation;

      operands = new ArrayList<Computation>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation x:
            source.get_operands()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         x.get_visited_by(cc);

         assimilate(cc);

         operands.add(cc.get_computation());
      }

      fate_op_name = source.get_operator().get_name();

      if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.PLUS.get_name()
         )
      )
      {
         final Iterator<Computation> operands_it;

         operands_it = operands.iterator();

         result_as_computation = operands_it.next();

         while (operands_it.hasNext())
         {
            result_as_computation =
               Operation.plus(operands_it.next(), result_as_computation);
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.MINUS.get_name()
         )
      )
      {
         final Iterator<Computation> operands_it;
         Computation sum;

         operands_it = operands.iterator();

         result_as_computation = operands_it.next();
         sum = operands_it.next();

         while (operands_it.hasNext())
         {
            sum = Operation.plus(operands_it.next(), sum);
         }

         result_as_computation = Operation.minus(result_as_computation, sum);
      }
      else if
      (
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.TIMES.get_name())
      )
      {
         final Iterator<Computation> operands_it;

         operands_it = operands.iterator();

         result_as_computation = operands_it.next();

         while (operands_it.hasNext())
         {
            result_as_computation =
               Operation.times(operands_it.next(), result_as_computation);
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.DIVIDE.get_name()
         )
      )
      {
         result_as_computation =
            Operation.divide(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.MIN.get_name()
         )
      )
      {
         final Register candidate;

         candidate = reserve(operands.get(0).get_type());
         result_as_address = candidate.get_address();
         result_as_computation = candidate.get_value();

         init_instructions.add
         (
            new SetValue(result_as_address, operands.get(0))
         );

         for (final Computation operand: operands)
         {
            init_instructions.add
            (
               new SetValue
               (
                  result_as_address,
                  new IfElseComputation
                  (
                     Operation.less_than
                     (
                        result_as_computation,
                        operand
                     ),
                     result_as_computation,
                     operand
                  )
               )
            );
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.MAX.get_name()
         )
      )
      {
         final Register candidate;

         candidate = reserve(operands.get(0).get_type());
         result_as_address = candidate.get_address();
         result_as_computation = candidate.get_value();

         init_instructions.add
         (
            new SetValue(result_as_address, operands.get(0))
         );

         for (final Computation operand: operands)
         {
            init_instructions.add
            (
               new SetValue
               (
                  result_as_address,
                  new IfElseComputation
                  (
                     Operation.greater_than(result_as_computation, operand),
                     result_as_computation,
                     operand
                  )
               )
            );
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.ABS.get_name()
         )
      )
      {
         final Computation zero, minus_one;

         if (operands.get(0).get_type().equals(Type.INT))
         {
            zero = Constant.ZERO;
            minus_one = new Constant(Type.INT, "-1");
         }
         else
         {
            zero = new Constant(Type.FLOAT, "0.0");
            minus_one = new Constant(Type.FLOAT, "-1.0");
         }

         result_as_computation =
            new IfElseComputation
            (
               Operation.greater_than(zero, operands.get(0)),
               Operation.times(minus_one, operands.get(0)),
               operands.get(0)
            );
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.CLAMP.get_name()
         )
      )
      {
         final Type t;
         final Register candidate;
         final Computation result_as_computation;

         t = operands.get(2).get_type();
         candidate = reserve(t);
         result_as_address = candidate.get_address();
         result_as_computation = candidate.get_value();

         init_instructions.add
         (
            new SetValue
            (
               result_as_address,
               new IfElseComputation
               (
                  Operation.greater_than(operands.get(2), operands.get(1)),
                  operands.get(1),
                  operands.get(2)
               )
            )
         );
         init_instructions.add
         (
            new SetValue
            (
               result_as_address,
               new IfElseComputation
               (
                  Operation.less_than(result_as_computation, operands.get(0)),
                  operands.get(0),
                  result_as_computation
               )
            )
         );
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.MODULO.get_name()
         )
      )
      {
         result_as_computation =
            Operation.modulo(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.POWER.get_name()
         )
      )
      {
         result_as_computation =
            Operation.power(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.RANDOM.get_name()
         )
      )
      {
         final List<Instruction> push_rand, pop_rand;
         final Register result, zero_holder;

         push_rand = new ArrayList<Instruction>();
         pop_rand = new ArrayList<Instruction>();

         result = reserve(Type.INT);
         zero_holder = reserve(Type.INT);

         result_as_computation = result.get_value();
         result_as_address = result.get_address();

         push_rand.add
         (
            new SetValue
            (
               result_as_address,
               Operation.rand(operands.get(0), operands.get(1))
            )
         );

         push_rand.add
         (
            new SetValue
            (
               new RelativeAddress
               (
                  compiler.registers().get_rand_value_holder().get_address(),
                  new Cast
                  (
                     new Size
                     (
                        compiler.registers().get_rand_value_holder
                        (
                        ).get_address()
                     ),
                     Type.STRING
                  ),
                  Type.INT
               ),
               result_as_computation
            )
         );

         pop_rand.add
         (
            new SetValue
            (
               result_as_address,
               new ValueOf
               (
                  new RelativeAddress
                  (
                     compiler.registers().get_rand_value_holder().get_address(),
                     new Cast(Constant.ZERO, Type.STRING),
                     Type.INT
                  )
               )
            )
         );

         pop_rand.add(new SetValue(zero_holder.get_address(), Constant.ZERO));

         pop_rand.add
         (
            tonkadur.wyrd.v1.compiler.util.RemoveAt.generate
            (
               compiler.registers(),
               compiler.assembler(),
               zero_holder.get_address(),
               new Size
               (
                  compiler.registers().get_rand_value_holder().get_address()
               ),
               compiler.registers().get_rand_value_holder().get_address()
            )
         );

         init_instructions.add
         (
            tonkadur.wyrd.v1.compiler.util.IfElse.generate
            (
               compiler.registers(),
               compiler.assembler(),
               Operation.equals
               (
                  compiler.registers().get_rand_mode_holder().get_value(),
                  Constant.ZERO
               ),
               new SetValue
               (
                  result_as_address,
                  Operation.rand(operands.get(0), operands.get(1))
               ),
               tonkadur.wyrd.v1.compiler.util.IfElse.generate
               (
                  compiler.registers(),
                  compiler.assembler(),
                  Operation.equals
                  (
                     compiler.registers().get_rand_mode_holder().get_value(),
                     Constant.ONE
                  ),
                  compiler.assembler().merge(push_rand),
                  compiler.assembler().merge(pop_rand)
               )
            )
         );
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.AND.get_name()
         )
      )
      {
         final Iterator<Computation> operands_it;

         operands_it = operands.iterator();

         result_as_computation = operands_it.next();

         while (operands_it.hasNext())
         {
            result_as_computation =
               Operation.and(operands_it.next(), result_as_computation);
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.OR.get_name()
         )
      )
      {
         final Iterator<Computation> operands_it;

         operands_it = operands.iterator();

         result_as_computation = operands_it.next();

         while (operands_it.hasNext())
         {
            result_as_computation =
               Operation.or(operands_it.next(), result_as_computation);
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.NOT.get_name()
         )
      )
      {
         result_as_computation = Operation.not(operands.get(0));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.IMPLIES.get_name()
         )
      )
      {
         result_as_computation =
            Operation.implies(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.ONE_IN.get_name()
         )
      )
      {
         final Iterator<Computation> operand_it;

         operand_it = operands.iterator();

         result_as_computation =
            new IfElseComputation
            (
               operand_it.next(),
               Constant.ONE,
               Constant.ZERO
            );

         while (operand_it.hasNext())
         {
            result_as_computation =
               Operation.plus
               (
                  new IfElseComputation
                  (
                     operand_it.next(),
                     Constant.ONE,
                     Constant.ZERO
                  ),
                  result_as_computation
               );
         }

         result_as_computation =
            Operation.equals(result_as_computation, Constant.ONE);
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.EQUALS.get_name()
         )
      )
      {
         final Iterator<Computation> operands_it;
         final Computation first_elem;

         operands_it = operands.iterator();

         first_elem = operands_it.next();

         result_as_computation =
            Operation.equals(first_elem, operands_it.next());

         while (operands_it.hasNext())
         {
            result_as_computation =
               Operation.and
               (
                  result_as_computation,
                  Operation.equals(first_elem, operands_it.next())
               );
         }
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.LOWER_THAN.get_name()
         )
      )
      {
         result_as_computation =
            Operation.less_than(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.LOWER_EQUAL_THAN.get_name()
         )
      )
      {
         result_as_computation =
            Operation.less_equal_than(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.GREATER_EQUAL_THAN.get_name()
         )
      )
      {
         result_as_computation =
            Operation.greater_equal_than(operands.get(0), operands.get(1));
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.GREATER_THAN.get_name()
         )
      )
      {
         result_as_computation =
            Operation.greater_than(operands.get(0), operands.get(1));
      }
      else
      {
         System.err.println("[P] Unknown Fate operator '" + fate_op_name+ "'.");
      }
   }
}
