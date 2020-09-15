package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tonkadur.error.Error;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.PointerType;
import tonkadur.wyrd.v1.lang.type.MapType;
import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;
import tonkadur.wyrd.v1.lang.instruction.SetPC;

import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.IfElse;
import tonkadur.wyrd.v1.compiler.util.If;
import tonkadur.wyrd.v1.compiler.util.Shuffle;
import tonkadur.wyrd.v1.compiler.util.RemoveAt;
import tonkadur.wyrd.v1.compiler.util.InsertAt;
import tonkadur.wyrd.v1.compiler.util.RemoveAllOf;
import tonkadur.wyrd.v1.compiler.util.RemoveOneOf;
import tonkadur.wyrd.v1.compiler.util.ReverseList;
import tonkadur.wyrd.v1.compiler.util.CreateCons;
import tonkadur.wyrd.v1.compiler.util.IterativeSearch;
import tonkadur.wyrd.v1.compiler.util.CountOccurrences;
import tonkadur.wyrd.v1.compiler.util.While;
import tonkadur.wyrd.v1.compiler.util.LambdaEvaluation;

import tonkadur.wyrd.v1.lang.computation.*;

import tonkadur.wyrd.v1.lang.World;
import tonkadur.wyrd.v1.lang.Register;

public class ComputationCompiler
implements tonkadur.fate.v1.lang.meta.ComputationVisitor
{
   protected final Compiler compiler;
   protected final List<Instruction> init_instructions;
   protected final Collection<Register> reserved_registers;
   protected boolean instructions_were_generated;
   protected Computation result_as_computation;
   protected Address result_as_address;

   public ComputationCompiler (final Compiler compiler)
   {
      this.compiler = compiler;

      reserved_registers = new ArrayList<Register>();
      init_instructions = new ArrayList<Instruction>();
      result_as_address = null;
      result_as_computation = null;
      instructions_were_generated = false;
   }

   public boolean has_init ()
   {
      return !init_instructions.isEmpty();
   }

   public Instruction get_init ()
   {
      instructions_were_generated = true;

      if (init_instructions.isEmpty())
      {
         return null;
      }

      return compiler.assembler().merge(init_instructions);
   }

   public Computation get_computation ()
   {
      if (result_as_computation != null)
      {
         return result_as_computation;
      }
      else
      {
         result_as_computation = new ValueOf(result_as_address);

         return result_as_computation;
      }
   }

   public Address get_address ()
   {
      if (result_as_address == null)
      {
         System.err.println("[P] Missing generate_address()!");
      }

      return result_as_address;
   }

   public void generate_address ()
   {
      if ((!instructions_were_generated) && (result_as_address == null))
      {
         final Register result;

         result = reserve(result_as_computation.get_type());

         init_instructions.add
         (
            new SetValue(result.get_address(), result_as_computation)
         );

         result_as_address = result.get_address();
         /* Avoids recomputations */
         result_as_computation = result.get_value();
      }
   }

   public void release_registers (final List<Instruction> instr_holder)
   {
      for (final Register reg: reserved_registers)
      {
         compiler.registers().release(reg, instr_holder);
      }
   }

   protected void assimilate (final ComputationCompiler cc)
   {
      init_instructions.addAll(cc.init_instructions);
      reserved_registers.addAll(cc.reserved_registers);
   }

   protected Register reserve (final Type t)
   {
      final Register result;

      result = compiler.registers().reserve(t, init_instructions);

      reserved_registers.add(result);

      return result;
   }

   @Override
   public void visit_at_reference
   (
      final tonkadur.fate.v1.lang.computation.AtReference n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_address =
         new Address
         (
            n_cc.get_computation(),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.PointerType)
                     n.get_parent().get_type()
               ).get_referenced_type()
            )
         );
   }

   @Override
   public void visit_cast
   (
      final tonkadur.fate.v1.lang.computation.Cast n
   )
   throws Throwable
   {
      final ComputationCompiler cc;
      final Type target_type;

      cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(cc);

      assimilate(cc);

      target_type = TypeCompiler.compile(compiler, n.get_type());

      if (target_type.equals(cc.get_computation().get_type()))
      {
         result_as_computation = cc.get_computation();
      }
      else
      {
         result_as_computation = new Cast(cc.get_computation(), target_type);
      }
   }

   @Override
   public void visit_cond_value
   (
      final tonkadur.fate.v1.lang.computation.CondValue n
   )
   throws Throwable
   {
      final List<Cons<ComputationCompiler, ComputationCompiler>> cc_list;
      boolean is_safe;

      cc_list = new ArrayList<Cons<ComputationCompiler, ComputationCompiler>>();
      is_safe = true;

      for
      (
         final Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Computation
         >
         branch:
            n.get_branches()
      )
      {
         final ComputationCompiler cond_cc, val_cc;

         cond_cc = new ComputationCompiler(compiler);
         val_cc = new ComputationCompiler(compiler);

         branch.get_car().get_visited_by(cond_cc);
         branch.get_cdr().get_visited_by(val_cc);

         is_safe = is_safe && !cond_cc.has_init() && !val_cc.has_init();

         reserved_registers.addAll(cond_cc.reserved_registers);
         reserved_registers.addAll(val_cc.reserved_registers);

         cc_list.add(new Cons(cond_cc, val_cc));
      }

      Collections.reverse(cc_list);

      if (is_safe)
      {
         final Iterator<Cons<ComputationCompiler, ComputationCompiler>> it;

         it = cc_list.iterator();

         result_as_computation = it.next().get_cdr().get_computation();

         while (it.hasNext())
         {
            final Cons<ComputationCompiler, ComputationCompiler> next;

            next = it.next();

            result_as_computation =
               new IfElseComputation
               (
                  next.get_car().get_computation(),
                  next.get_cdr().get_computation(),
                  result_as_computation
               );
         }
      }
      else
      {
         final Iterator<Cons<ComputationCompiler, ComputationCompiler>> it;
         final Register result;
         Cons<ComputationCompiler, ComputationCompiler> next;
         List<Instruction> new_value, new_cond;
         Instruction prev_branch;


         it = cc_list.iterator();
         next = it.next();

         result = reserve(next.get_cdr().get_computation().get_type());
         result_as_address = result.get_address();

         new_value = new ArrayList<Instruction>();

         if (next.get_cdr().has_init())
         {
            new_value.add(next.get_cdr().get_init());
         }

         new_value.add
         (
            new SetValue(result_as_address, next.get_cdr().get_computation())
         );

         prev_branch = compiler.assembler().merge(new_value);

         while (it.hasNext())
         {
            next = it.next();

            new_value = new ArrayList<Instruction>();
            new_cond = new ArrayList<Instruction>();

            if (next.get_car().has_init())
            {
               new_cond.add(next.get_car().get_init());
            }

            if (next.get_cdr().has_init())
            {
               new_value.add(next.get_cdr().get_init());
            }

            new_value.add
            (
               new SetValue(result_as_address, next.get_cdr().get_computation())
            );

            new_cond.add
            (
               IfElse.generate
               (
                  compiler.registers(),
                  compiler.assembler(),
                  next.get_car().get_computation(),
                  compiler.assembler().merge(new_value),
                  prev_branch
               )
            );

            prev_branch = compiler.assembler().merge(new_cond);
         }

         init_instructions.add(prev_branch);

         result_as_computation = result.get_value();
      }
   }

   @Override
   public void visit_constant
   (
      final tonkadur.fate.v1.lang.computation.Constant n
   )
   throws Throwable
   {
      result_as_computation =
         new Constant
         (
            TypeCompiler.compile(compiler, n.get_type()),
            n.get_value_as_string()
         );
   }

   @Override
   public void visit_count_operator
   (
      final tonkadur.fate.v1.lang.computation.CountOperator n
   )
   throws Throwable
   {
      final ComputationCompiler collection_compiler, element_compiler;

      collection_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_compiler);
      n.get_element().get_visited_by(element_compiler);

      collection_compiler.generate_address();

      assimilate(collection_compiler);
      assimilate(element_compiler);

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
         ).is_set()
      )
      {
         final Register was_found, index, element;

         was_found = reserve(Type.BOOL);
         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue
            (
               element.get_address(),
               element_compiler.get_computation()
            )
         );
         init_instructions.add
         (
            BinarySearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element.get_value(),
               new Size(collection_compiler.get_address()),
               collection_compiler.get_address(),
               was_found.get_address(),
               index.get_address()
            )
         );

         result_as_computation =
            new IfElseComputation
            (
               was_found.get_value(),
               Constant.ONE,
               Constant.ZERO
            );
      }
      else
      {
         final Register result, element;

         result = reserve(Type.INT);

         result_as_address = result.get_address();
         result_as_computation = result.get_value();

         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue
            (
               element.get_address(),
               element_compiler.get_computation()
            )
         );
         init_instructions.add
         (
            CountOccurrences.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element.get_value(),
               new Size(collection_compiler.get_address()),
               collection_compiler.get_address(),
               result_as_address
            )
         );
      }
   }

   @Override
   public void visit_field_reference
   (
      final tonkadur.fate.v1.lang.computation.FieldReference n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_address =
         new RelativeAddress
         (
            n_cc.get_address(),
            new Constant(Type.STRING, n.get_field_name()),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.DictType)
                     n.get_parent().get_type()
               ).get_field_type(null, n.get_field_name())
            )
         );
   }

   @Override
   public void visit_if_else_value
   (
      final tonkadur.fate.v1.lang.computation.IfElseValue n
   )
   throws Throwable
   {
      final ComputationCompiler cond_cc, if_true_cc, if_false_cc;

      cond_cc = new ComputationCompiler(compiler);
      if_true_cc = new ComputationCompiler(compiler);
      if_false_cc = new ComputationCompiler(compiler);

      n.get_condition().get_visited_by(cond_cc);
      n.get_if_true().get_visited_by(if_true_cc);
      n.get_if_false().get_visited_by(if_false_cc);

      if (if_true_cc.has_init() || if_false_cc.has_init())
      {
         /*
          * Unsafe ifelse computation: at least one of the branches needs to
          * use instructions with values *before* the condition has been
          * checked. This results in non-lazy evaluation, and is dangerous:
          * the condition might be a test to ensure that the computations of the
          * chosen branch are legal. In such cases, performing the potentially
          * illegal branch's instructions is likely to result in a runtime error
          * on the interpreter.
          *
          * Instead, we just convert the ifelse into an instruction-based
          * equivalent and store the result in an anonymous register to be used
          * here.
          */
         final Register if_else_result;
         final List<Instruction> if_true_branch;
         final List<Instruction> if_false_branch;

         if_else_result = reserve(if_true_cc.get_computation().get_type());

         if_true_branch = new ArrayList<Instruction>();
         if_false_branch = new ArrayList<Instruction>();

         if (if_true_cc.has_init())
         {
            if_true_branch.add(if_true_cc.get_init());
         }

         if (if_false_cc.has_init())
         {
            if_false_branch.add(if_false_cc.get_init());
         }

         if_true_branch.add
         (
            new SetValue
            (
               if_else_result.get_address(),
               if_true_cc.get_computation()
            )
         );

         if_false_branch.add
         (
            new SetValue
            (
               if_else_result.get_address(),
               if_false_cc.get_computation()
            )
         );

         if (cond_cc.has_init())
         {
            init_instructions.add(cond_cc.get_init());
         }

         init_instructions.add
         (
            IfElse.generate
            (
               compiler.registers(),
               compiler.assembler(),
               cond_cc.get_computation(),
               compiler.assembler().merge(if_true_branch),
               compiler.assembler().merge(if_false_branch)
            )
         );

         reserved_registers.addAll(cond_cc.reserved_registers);
         reserved_registers.addAll(if_true_cc.reserved_registers);
         reserved_registers.addAll(if_false_cc.reserved_registers);

         result_as_computation = if_else_result.get_value();
         result_as_address = if_else_result.get_address();
      }
      else
      {
         assimilate(cond_cc);
         assimilate(if_true_cc);
         assimilate(if_false_cc);

         result_as_computation =
            new IfElseComputation
            (
               cond_cc.get_computation(),
               if_true_cc.get_computation(),
               if_false_cc.get_computation()
            );
      }
   }

   @Override
   public void visit_is_member_operator
   (
      final tonkadur.fate.v1.lang.computation.IsMemberOperator n
   )
   throws Throwable
   {
      final Register result;
      final ComputationCompiler collection_compiler, element_compiler;

      collection_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_compiler);
      n.get_element().get_visited_by(element_compiler);

      collection_compiler.generate_address();

      assimilate(collection_compiler);
      assimilate(element_compiler);

      result = reserve(Type.BOOL);
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
         ).is_set()
      )
      {
         final Register index, element;

         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue
            (
               element.get_address(),
               element_compiler.get_computation()
            )
         );
         init_instructions.add
         (
            BinarySearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element.get_value(),
               new Size(collection_compiler.get_address()),
               collection_compiler.get_address(),
               result_as_address,
               index.get_address()
            )
         );

      }
      else
      {
         final Register index, element;

         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue
            (
               element.get_address(),
               element_compiler.get_computation()
            )
         );
         init_instructions.add
         (
            IterativeSearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element.get_value(),
               new Size(collection_compiler.get_address()),
               collection_compiler.get_address(),
               result_as_address,
               index.get_address()
            )
         );
      }
   }

   @Override
   public void visit_newline
   (
      final tonkadur.fate.v1.lang.computation.Newline n
   )
   throws Throwable
   {
      result_as_computation = new Newline();
   }

   @Override
   public void visit_operation
   (
      final tonkadur.fate.v1.lang.computation.Operation n
   )
   throws Throwable
   {
      final String fate_op_name;
      final List<Computation> operands;

      operands = new ArrayList<Computation>();

      for (final tonkadur.fate.v1.lang.meta.Computation x: n.get_operands())
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         x.get_visited_by(cc);

         assimilate(cc);

         operands.add(cc.get_computation());
      }

      fate_op_name = n.get_operator().get_name();

      if
      (
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.PLUS.get_name())
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
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.MINUS.get_name())
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
                     Operation.less_than(result_as_computation, operand),
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
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.POWER.get_name())
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
            RemoveAt.generate
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
            IfElse.generate
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
               IfElse.generate
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
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.AND.get_name())
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
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.OR.get_name())
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
         fate_op_name.equals(tonkadur.fate.v1.lang.computation.Operator.NOT.get_name())
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
         result_as_computation =
            Operation.equals(operands.get(0), operands.get(1));
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

   @Override
   public void visit_size_operator
   (
      final tonkadur.fate.v1.lang.computation.SizeOperator n
   )
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(cc);

      assimilate(cc);

      result_as_computation = new Size(cc.get_address());
   }

   @Override
   public void visit_is_empty
   (
      final tonkadur.fate.v1.lang.computation.IsEmpty n
   )
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(cc);

      assimilate(cc);

      result_as_computation =
         Operation.equals(new Size(cc.get_address()), Constant.ZERO);
   }

   @Override
   public void visit_index_of_operator
   (
      final tonkadur.fate.v1.lang.computation.IndexOfOperator n
   )
   throws Throwable
   {
      final ComputationCompiler elem_cc, collection_cc;
      final Register result, result_found;

      result = reserve(Type.INT);
      result_found = reserve(Type.BOOL);

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      assimilate(elem_cc);
      assimilate(collection_cc);

      init_instructions.add
      (
         IterativeSearch.generate
         (
            compiler.registers(),
            compiler.assembler(),
            elem_cc.get_computation(),
            new Size(collection_cc.get_address()),
            collection_cc.get_address(),
            result_found.get_address(),
            result_as_address
         )
      );
   }

   @Override
   public void visit_new
   (
      final tonkadur.fate.v1.lang.computation.New n
   )
   throws Throwable
   {
      result_as_computation =
         new New(TypeCompiler.compile(compiler, n.get_target_type()));
   }

   @Override
   public void visit_access
   (
      final tonkadur.fate.v1.lang.computation.Access n
   )
   throws Throwable
   {
      final ComputationCompiler extra_address_cc, base_address_cc;

      base_address_cc = new ComputationCompiler(compiler);
      extra_address_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(base_address_cc);
      n.get_index().get_visited_by(extra_address_cc);

      assimilate(base_address_cc);
      assimilate(extra_address_cc);

      result_as_address =
         new RelativeAddress
         (
            base_address_cc.get_address(),
            new Cast(extra_address_cc.get_computation(), Type.STRING),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.CollectionType)
                     n.get_parent().get_type()
               ).get_content_type()
            )
         );
   }

   @Override
   public void visit_switch_value
   (
      final tonkadur.fate.v1.lang.computation.SwitchValue n
   )
   throws Throwable
   {
      final ComputationCompiler target_cc, default_cc;
      final List<Cons<ComputationCompiler, ComputationCompiler>> cc_list;
      boolean is_safe;

      target_cc = new ComputationCompiler(compiler);
      default_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      target_cc.generate_address();

      assimilate(target_cc);

      cc_list = new ArrayList<Cons<ComputationCompiler, ComputationCompiler>>();
      is_safe = true;

      n.get_default().get_visited_by(default_cc);

      for
      (
         final Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Computation
         >
         branch:
            n.get_branches()
      )
      {
         final ComputationCompiler candidate_cc, val_cc;

         candidate_cc = new ComputationCompiler(compiler);
         val_cc = new ComputationCompiler(compiler);

         branch.get_car().get_visited_by(candidate_cc);
         branch.get_cdr().get_visited_by(val_cc);

         is_safe = is_safe && !candidate_cc.has_init() && !val_cc.has_init();

         reserved_registers.addAll(candidate_cc.reserved_registers);
         reserved_registers.addAll(val_cc.reserved_registers);

         cc_list.add(new Cons(candidate_cc, val_cc));
      }

      is_safe = is_safe && !default_cc.has_init();
      reserved_registers.addAll(default_cc.reserved_registers);

      Collections.reverse(cc_list);

      if (is_safe)
      {
         final Iterator<Cons<ComputationCompiler, ComputationCompiler>> it;

         it = cc_list.iterator();

         result_as_computation = default_cc.get_computation();

         while (it.hasNext())
         {
            final Cons<ComputationCompiler, ComputationCompiler> next;

            next = it.next();

            result_as_computation =
               new IfElseComputation
               (
                  Operation.equals
                  (
                     next.get_car().get_computation(),
                     target_cc.get_computation()
                  ),
                  next.get_cdr().get_computation(),
                  result_as_computation
               );
         }
      }
      else
      {
         final Iterator<Cons<ComputationCompiler, ComputationCompiler>> it;
         final Register result;
         Cons<ComputationCompiler, ComputationCompiler> next;
         List<Instruction> new_value, new_cond;
         Instruction prev_branch;

         it = cc_list.iterator();


         new_value = new ArrayList<Instruction>();

         if (default_cc.has_init())
         {
            new_value.add(default_cc.get_init());
         }

         result = reserve(default_cc.get_computation().get_type());
         result_as_address = result.get_address();

         new_value.add
         (
            new SetValue(result_as_address, default_cc.get_computation())
         );

         prev_branch = compiler.assembler().merge(new_value);

         while (it.hasNext())
         {
            next = it.next();

            new_value = new ArrayList<Instruction>();
            new_cond = new ArrayList<Instruction>();

            if (next.get_car().has_init())
            {
               new_cond.add(next.get_car().get_init());
            }

            if (next.get_cdr().has_init())
            {
               new_value.add(next.get_cdr().get_init());
            }

            new_value.add
            (
               new SetValue(result_as_address, next.get_cdr().get_computation())
            );

            new_cond.add
            (
               IfElse.generate
               (
                  compiler.registers(),
                  compiler.assembler(),
                  Operation.equals
                  (
                     next.get_car().get_computation(),
                     target_cc.get_computation()
                  ),
                  compiler.assembler().merge(new_value),
                  prev_branch
               )
            );

            prev_branch = compiler.assembler().merge(new_cond);
         }

         init_instructions.add(prev_branch);

         result_as_computation = result.get_value();
      }
   }

   @Override
   public void visit_paragraph
   (
      final tonkadur.fate.v1.lang.computation.Paragraph n
   )
   throws Throwable
   {
      final List<Computation> content;

      content = new ArrayList<Computation>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_content:
            n.get_content()
      )
      {
         final ComputationCompiler content_cc;

         content_cc = new ComputationCompiler(compiler);

         fate_content.get_visited_by(content_cc);

         assimilate(content_cc);

         content.add(content_cc.get_computation());
      }

      result_as_computation = new RichText(content);
   }

   @Override
   public void visit_address_operator
   (
      final tonkadur.fate.v1.lang.computation.AddressOperator n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_computation = n_cc.get_address();
   }

   @Override
   public void visit_text_with_effect
   (
      final tonkadur.fate.v1.lang.computation.TextWithEffect n
   )
   throws Throwable
   {
      final ComputationCompiler text_cc;
      final List<Computation> parameters;

      text_cc = new ComputationCompiler(compiler);
      parameters = new ArrayList<Computation>();

      n.get_text().get_visited_by(text_cc);

      assimilate(text_cc);

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_param:
            n.get_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         fate_param.get_visited_by(param_cc);

         assimilate(param_cc);

         parameters.add(param_cc.get_computation());
      }

      result_as_computation =
         new AddRichTextEffect
         (
            n.get_effect().get_name(),
            parameters,
            Collections.singletonList(text_cc.get_computation())
         );
   }

   @Override
   public void visit_value_to_rich_text
   (
      final tonkadur.fate.v1.lang.computation.ValueToRichText n
   )
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler(compiler);

      n.get_value().get_visited_by(cc);

      assimilate(cc);

      result_as_computation =
         new RichText(Collections.singletonList(cc.get_computation()));
   }

   @Override
   public void visit_lambda_expression
   (
      final tonkadur.fate.v1.lang.computation.LambdaExpression n
   )
   throws Throwable
   {
      final String out_label, in_label;
      final List<Register> parameters;
      final Register result_holder;
      final ComputationCompiler expr_compiler;
      final Type result_type;
      final String context_name;

      out_label = compiler.assembler().generate_label("<lambda_expr#out>");
      in_label = compiler.assembler().generate_label("<lambda_expr#in>");

      parameters = new ArrayList<Register>();

      context_name = compiler.registers().create_stackable_context_name();

      compiler.registers().create_stackable_context
      (
         context_name,
         init_instructions
      );

      init_instructions.add
      (
         compiler.assembler().mark_after
         (
            new SetPC(compiler.assembler().get_label_constant(out_label)),
            in_label
         )
      );

      init_instructions.addAll
      (
         compiler.registers().get_initialize_context_instructions(context_name)
      );

      compiler.registers().push_context(context_name);

      result_type =
         TypeCompiler.compile(compiler, n.get_lambda_function().get_type());

      result_holder =
         /* Defining a context, don't use this.reserve(), otherwise this
          * context-dependent variable will get released outside the context
          * (where it doesn't exist) and that will cause an error.
          */
         compiler.registers().reserve
         (
            new PointerType(result_type),
            init_instructions
         );

      parameters.add(result_holder);

      for (final tonkadur.fate.v1.lang.Variable param: n.get_parameters())
      {
         final Register r;

         r =
            /* Bound registers, can't use this.reserve() */
            compiler.registers().reserve
            (
               TypeCompiler.compile(compiler, param.get_type()),
               init_instructions
            );

         parameters.add(r);

         compiler.registers().bind(param.get_name(), r);
      }

      init_instructions.addAll
      (
         compiler.registers().read_parameters(parameters)
      );

      expr_compiler = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(expr_compiler);

      /* Nope: its variables are removed when the context is left. */
      /* assimilate(expr_compiler); */
      if (expr_compiler.has_init())
      {
         init_instructions.add(expr_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue
         (
            new Address(result_holder.get_value(), result_type),
            expr_compiler.get_computation()
         )
      );
/*
 * No need: it's getting finalized anyway.
      for (final tonkadur.fate.v1.lang.Variable param: n.get_parameters())
      {
         compiler.registers().unbind(param.get_name(), init_instructions);
      }
*/
      init_instructions.addAll
      (
         compiler.registers().get_finalize_context_instructions()
      );

      init_instructions.add
      (
         compiler.assembler().mark_after
         (
            compiler.assembler().merge
            (
               compiler.registers().get_leave_context_instructions()
            ),
            out_label
         )
      );

      compiler.registers().pop_context();

      result_as_computation = compiler.assembler().get_label_constant(in_label);
   }

   @Override
   public void visit_lambda_evaluation
   (
      final tonkadur.fate.v1.lang.computation.LambdaEvaluation n
   )
   throws Throwable
   {
      final ComputationCompiler target_line_cc;
      final List<Computation> parameters;
      final Register result;

      parameters = new ArrayList<Computation>();
      target_line_cc = new ComputationCompiler(compiler);

      n.get_lambda_function_reference().get_visited_by(target_line_cc);

      assimilate(target_line_cc);

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation param: n.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         param.get_visited_by(cc);

         assimilate(cc);

         parameters.add(cc.get_computation());
      }

      init_instructions.add
      (
         LambdaEvaluation.generate
         (
            compiler.registers(),
            compiler.assembler(),
            target_line_cc.get_computation(),
            result_as_address,
            parameters
         )
      );
   }

   @Override
   public void visit_let
   (
      final tonkadur.fate.v1.lang.computation.Let n
   )
   throws Throwable
   {
      final Collection<String> names;

      names = new ArrayList<String>();

      for
      (
         final
            Cons
            <
               tonkadur.fate.v1.lang.Variable,
               tonkadur.fate.v1.lang.meta.Computation
            >
            a:
               n.get_assignments()
      )
      {
         final String name;
         final Register r;
         final ComputationCompiler cc;

         name = a.get_car().get_name();
         r =
            /* These are free by the unbind below */
            compiler.registers().reserve
            (
               TypeCompiler.compile(compiler, a.get_car().get_type()),
               init_instructions
            );

         compiler.registers().bind(name, r);
         names.add(name);

         cc = new ComputationCompiler(compiler);

         a.get_cdr().get_visited_by(cc);

         assimilate(cc);

         init_instructions.add
         (
            new SetValue(r.get_address(), cc.get_computation())
         );
      }

      n.get_computation().get_visited_by(this);

      for (final String name: names)
      {
         compiler.registers().unbind(name, init_instructions);
      }
   }

   @Override
   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.computation.VariableReference n
   )
   throws Throwable
   {
      final Register register;

      register =
         compiler.registers().get_context_register(n.get_variable().get_name());

      result_as_address = register.get_address();
      result_as_computation = register.get_value();
   }

   @Override
   public void visit_car_cdr
   (
      final tonkadur.fate.v1.lang.computation.CarCdr n
   )
   throws Throwable
   {
      final ComputationCompiler address_cc;

      address_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(address_cc);

      assimilate(address_cc);

      result_as_address =
         new RelativeAddress
         (
            address_cc.get_address(),
            new Constant
            (
               Type.STRING,
               (n.is_car()? "0" : "1")
            ),
            TypeCompiler.compile(compiler, n.get_type())
         );
   }

   @Override
   public void visit_cons
   (
      final tonkadur.fate.v1.lang.computation.ConsComputation n
   )
   throws Throwable
   {
      final Address car_addr, cdr_addr;
      final ComputationCompiler car_compiler, cdr_compiler;
      final Register result;

      result = reserve(DictType.WILD);
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      car_compiler = new ComputationCompiler(compiler);

      n.get_car().get_visited_by(car_compiler);

      if (car_compiler.has_init())
      {
         init_instructions.add(car_compiler.get_init());
      }

      cdr_compiler = new ComputationCompiler(compiler);

      n.get_cdr().get_visited_by(cdr_compiler);

      if (cdr_compiler.has_init())
      {
         init_instructions.add(cdr_compiler.get_init());
      }

      init_instructions.add
      (
         CreateCons.generate
         (
            compiler.registers(),
            compiler.assembler(),
            result_as_address,
            car_compiler.get_computation(),
            cdr_compiler.get_computation()
         )
      );

      car_compiler.release_registers(init_instructions);
      cdr_compiler.release_registers(init_instructions);
   }

   @Override
   public void visit_add_element
   (
      final tonkadur.fate.v1.lang.computation.AddElementComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_add_element_at
   (
      final tonkadur.fate.v1.lang.computation.AddElementAtComputation n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, index_compiler;
      final ComputationCompiler element_compiler;
      final Register result, index, collection_size;

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      index_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      n.get_index().get_visited_by(index_compiler);

      index_compiler.generate_address();

      if (index_compiler.has_init())
      {
         init_instructions.add(index_compiler.get_init());
      }

      n.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         init_instructions.add(element_compiler.get_init());
      }

      collection_size = reserve(Type.INT);
      index = reserve(Type.INT);

      init_instructions.add
      (
         new SetValue
         (
            collection_size.get_address(),
            new Size(result_as_address)
         )
      );

      init_instructions.add
      (
         new SetValue
         (
            index.get_address(),
            new IfElseComputation
            (
               Operation.greater_than
               (
                  index_compiler.get_computation(),
                  collection_size.get_value()
               ),
               collection_size.get_value(),
               index_compiler.get_computation()
            )
         )
      );

      init_instructions.add
      (
         InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index.get_address(),
            element_compiler.get_computation(),
            collection_size.get_value(),
            result_as_address
         )
      );
   }

   @Override
   public void visit_add_elements_of
   (
      final tonkadur.fate.v1.lang.computation.AddElementsOfComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_fold
   (
      final tonkadur.fate.v1.lang.computation.Fold n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_map
   (
      final tonkadur.fate.v1.lang.computation.MapComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_range
   (
      final tonkadur.fate.v1.lang.computation.Range n
   )
   throws Throwable
   {
      final List<Instruction> while_body;
      final ComputationCompiler start_cc, end_cc, inc_cc;
      final Register result, iterator, accumulator;
      final Address new_element_addr;

      while_body = new ArrayList<Instruction>();

      result = reserve(MapType.MAP_TO_INT);
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      start_cc = new ComputationCompiler(compiler);
      end_cc = new ComputationCompiler(compiler);
      inc_cc = new ComputationCompiler(compiler);

      n.get_start().get_visited_by(start_cc);
      start_cc.generate_address();

      n.get_end().get_visited_by(end_cc);
      end_cc.generate_address();

      n.get_increment().get_visited_by(inc_cc);
      inc_cc.generate_address();

      assimilate(start_cc);
      assimilate(end_cc);
      assimilate(inc_cc);

      iterator = reserve(Type.INT);
      accumulator = reserve(Type.INT);

      init_instructions.add
      (
         new SetValue(iterator.get_address(), Constant.ZERO)
      );
      init_instructions.add
      (
         new SetValue(accumulator.get_address(), start_cc.get_computation())
      );

      new_element_addr =
         new RelativeAddress
         (
            result_as_address,
            new Cast(iterator.get_value(), Type.STRING),
            Type.INT
         );

      while_body.add(new Initialize(new_element_addr));
      while_body.add
      (
         new SetValue(new_element_addr, accumulator.get_value())
      );
      while_body.add
      (
         new SetValue
         (
            accumulator.get_address(),
            Operation.plus(accumulator.get_value(), inc_cc.get_computation())
         )
      );
      while_body.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.plus(iterator.get_value(), Constant.ONE)
         )
      );

      init_instructions.add
      (
         While.generate
         (
            compiler.registers(),
            compiler.assembler(),
            Operation.or
            (
               Operation.and
               (
                  Operation.greater_than
                  (
                     inc_cc.get_computation(),
                     Constant.ZERO
                  ),
                  Operation.less_equal_than
                  (
                     accumulator.get_value(),
                     end_cc.get_computation()
                  )
               ),
               Operation.and
               (
                  Operation.less_equal_than
                  (
                     inc_cc.get_computation(),
                     Constant.ZERO
                  ),
                  Operation.greater_equal_than
                  (
                     accumulator.get_value(),
                     end_cc.get_computation()
                  )
               )
            ),
            compiler.assembler.merge(while_body)
         )
      );
   }

   @Override
   public void visit_remove_all_of_element
   (
      final tonkadur.fate.v1.lang.computation.RemoveAllOfElementComputation n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, target_compiler;
      final Address collection_address;
      final Register result;

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      target_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      n.get_element().get_visited_by(target_compiler);

      assimilate(target_compiler);

      init_instructions.add
      (
         RemoveAllOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            target_compiler.get_computation(),
            new Size(result_as_address),
            result_as_address
         )
      );
   }

   @Override
   public void visit_remove_element_at
   (
      final tonkadur.fate.v1.lang.computation.RemoveElementAtComputation n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, target_compiler;
      final Address collection_address;
      final Register result, target_index;

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      target_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      n.get_index().get_visited_by(target_compiler);

      target_compiler.generate_address();

      assimilate(target_compiler);

      init_instructions.add
      (
         RemoveAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            target_compiler.get_address(),
            new Size(result_as_address),
            result_as_address
         )
      );
   }

   @Override
   public void visit_remove_element
   (
      final tonkadur.fate.v1.lang.computation.RemoveElementComputation n
   )
   throws Throwable
   {
      final ComputationCompiler elem_cc, collection_cc;
      final Register result;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      n.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         init_instructions.add(collection_cc.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, collection_cc.get_computation())
      );

      collection_cc.release_registers(init_instructions);

      n.get_element().get_visited_by(elem_cc);

      elem_cc.generate_address();

      assimilate(elem_cc);

      init_instructions.add
      (
         RemoveOneOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            elem_cc.get_computation(),
            result_as_address,
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
            ).is_set()
         )
      );
   }

   @Override
   public void visit_reverse_list
   (
      final tonkadur.fate.v1.lang.computation.ReverseListComputation n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler;
      final Register result;

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      init_instructions.add
      (
         ReverseList.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new Size(result_as_address),
            result_as_address
         )
      );
   }

   @Override
   public void visit_shuffle
   (
      final tonkadur.fate.v1.lang.computation.ShuffleComputation n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler;
      final Register result;

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      init_instructions.add
      (
         Shuffle.generate
         (
            compiler.registers(),
            compiler.assembler(),
            result_as_address
         )
      );
   }

   @Override
   public void visit_merge
   (
      final tonkadur.fate.v1.lang.computation.MergeComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_sublist
   (
      final tonkadur.fate.v1.lang.computation.SubListComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_partition
   (
      final tonkadur.fate.v1.lang.computation.PartitionComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_sort
   (
      final tonkadur.fate.v1.lang.computation.SortComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_filter
   (
      final tonkadur.fate.v1.lang.computation.FilterComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_indexed_map
   (
      final tonkadur.fate.v1.lang.computation.IndexedMapComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_push_element
   (
      final tonkadur.fate.v1.lang.computation.PushElementComputation n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, element_compiler;
      final Register result, collection_size, index;

      result = reserve(TypeCompiler.compile(compiler, n.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      address_compiler.release_registers(init_instructions);

      n.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         init_instructions.add(element_compiler.get_init());
      }

      collection_size = reserve(Type.INT);
      index = reserve(Type.INT);

      init_instructions.add
      (
         new SetValue
         (
            collection_size.get_address(),
            new Size(result_as_address)
         )
      );

      init_instructions.add
      (
         new SetValue
         (
            index.get_address(),
            (n.is_from_left() ? Constant.ZERO : collection_size.get_value())
         )
      );

      init_instructions.add
      (
         InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index.get_address(),
            element_compiler.get_computation(),
            collection_size.get_value(),
            result_as_address
         )
      );
   }

   @Override
   public void visit_pop_element
   (
      final tonkadur.fate.v1.lang.computation.PopElementComputation n
   )
   throws Throwable
   {
      /* TODO */
   }

   @Override
   public void visit_set_fields
   (
      final tonkadur.fate.v1.lang.computation.SetFieldsComputation n
   )
   throws Throwable
   {
      final ComputationCompiler target_cc;
      final Register result;

      target_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      target_cc.generate_address();

      if (target_cc.has_init())
      {
         init_instructions.add(target_cc.get_init());
      }

      result = reserve(target_cc.get_address().get_target_type());
      result_as_computation = result.get_value();
      result_as_address = result.get_address();

      init_instructions.add
      (
         new SetValue(result_as_address, target_cc.get_computation())
      );

      target_cc.release_registers(init_instructions);

      for
      (
         final Cons<String, tonkadur.fate.v1.lang.meta.Computation> entry:
            n.get_assignments()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         entry.get_cdr().get_visited_by(cc);

         if (cc.has_init())
         {
            init_instructions.add(cc.get_init());
         }

         init_instructions.add
         (
            new SetValue
            (
               new RelativeAddress
               (
                  result_as_address,
                  new Constant(Type.STRING, entry.get_car()),
                  cc.get_computation().get_type()
               ),
               cc.get_computation()
            )
         );

         cc.release_registers(init_instructions);
      }
   }
}
