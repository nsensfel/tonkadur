package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import tonkadur.error.Error;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.IfElse;
import tonkadur.wyrd.v1.compiler.util.IterativeSearch;
import tonkadur.wyrd.v1.compiler.util.CountOccurrences;


import tonkadur.wyrd.v1.lang.computation.*;


import tonkadur.wyrd.v1.lang.World;

public class ComputationCompiler
implements tonkadur.fate.v1.lang.meta.ComputationVisitor
{
   protected final Compiler compiler;
   protected final List<Instruction> init_instructions;
   protected final List<Ref> reserved_variables;
   protected boolean instructions_were_generated;
   protected Computation result_as_computation;
   protected Ref result_as_ref;

   public ComputationCompiler (final Compiler compiler)
   {
      this.compiler = compiler;

      reserved_variables = new ArrayList<Ref>();
      init_instructions = new ArrayList<Instruction>();
      result_as_ref = null;
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
         result_as_computation = new ValueOf(result_as_ref);

         return result_as_computation;
      }
   }

   public Ref get_ref ()
   {
      if (result_as_ref == null)
      {
         System.err.println("[P] Missing generate_ref()!");
      }

      return result_as_ref;
   }

   public void generate_ref ()
   {
      if ((!instructions_were_generated) && (result_as_ref == null))
      {
         final Ref result;

         result = reserve(result_as_computation.get_type());

         init_instructions.add
         (
            new SetValue(result, result_as_computation)
         );


         result_as_ref = result;
      }
   }

   public void release_variables ()
   {
      for (final Ref ref: reserved_variables)
      {
         compiler.anonymous_variables().release(ref);
      }
   }

   protected void assimilate (final ComputationCompiler cc)
   {
      init_instructions.addAll(cc.init_instructions);
      reserved_variables.addAll(cc.reserved_variables);
   }

   protected Ref reserve (final Type t)
   {
      final Ref result;

      result = compiler.anonymous_variables().reserve(t);

      reserved_variables.add(result);

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

      result_as_ref =
         new Ref
         (
            n_cc.get_computation(),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.RefType)
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

         reserved_variables.addAll(cond_cc.reserved_variables);
         reserved_variables.addAll(val_cc.reserved_variables);

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
         final Ref result;
         Cons<ComputationCompiler, ComputationCompiler> next;
         List<Instruction> new_value, new_cond;
         Instruction prev_branch;


         it = cc_list.iterator();
         next = it.next();

         result = reserve(next.get_cdr().get_computation().get_type());

         new_value = new ArrayList<Instruction>();

         if (next.get_cdr().has_init())
         {
            new_value.add(next.get_cdr().get_init());
         }

         new_value.add(new SetValue(result, next.get_cdr().get_computation()));

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
               new SetValue(result, next.get_cdr().get_computation())
            );

            new_cond.add
            (
               IfElse.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler(),
                  next.get_car().get_computation(),
                  compiler.assembler().merge(new_value),
                  prev_branch
               )
            );

            prev_branch = compiler.assembler().merge(new_cond);
         }

         init_instructions.add(prev_branch);

         result_as_computation = new ValueOf(result);
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

      collection_compiler.generate_ref();

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
         final Ref was_found, index, element;

         was_found = reserve(Type.BOOLEAN);
         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue(element, element_compiler.get_computation())
         );
         init_instructions.add
         (
            BinarySearch.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(element),
               new Size(collection_compiler.get_ref()),
               collection_compiler.get_ref(),
               was_found,
               index
            )
         );

         result_as_computation =
            new IfElseComputation
            (
               new ValueOf(was_found),
               Constant.ONE,
               Constant.ZERO
            );
      }
      else
      {
         final Ref element;

         result_as_ref = reserve(Type.INT);

         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue(element, element_compiler.get_computation())
         );
         init_instructions.add
         (
            CountOccurrences.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(element),
               new Size(collection_compiler.get_ref()),
               collection_compiler.get_ref(),
               result_as_ref
            )
         );
         result_as_computation = new ValueOf(result_as_ref);
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

      result_as_ref =
         new RelativeRef
         (
            n_cc.get_ref(),
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
          * equivalent and store the result in an anonymous variable to be used
          * here.
          */
         final Ref if_else_result;
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
            new SetValue(if_else_result, if_true_cc.get_computation())
         );

         if_false_branch.add
         (
            new SetValue(if_else_result, if_false_cc.get_computation())
         );

         if (cond_cc.has_init())
         {
            init_instructions.add(cond_cc.get_init());
         }

         init_instructions.add
         (
            IfElse.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               cond_cc.get_computation(),
               compiler.assembler().merge(if_true_branch),
               compiler.assembler().merge(if_false_branch)
            )
         );

         reserved_variables.addAll(cond_cc.reserved_variables);
         reserved_variables.addAll(if_true_cc.reserved_variables);
         reserved_variables.addAll(if_false_cc.reserved_variables);

         result_as_computation = new ValueOf(if_else_result);
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
      final ComputationCompiler collection_compiler, element_compiler;

      collection_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_compiler);
      n.get_element().get_visited_by(element_compiler);

      collection_compiler.generate_ref();

      assimilate(collection_compiler);
      assimilate(element_compiler);

      result_as_ref = reserve(Type.BOOLEAN);

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
         ).is_set()
      )
      {
         final Ref index, element;

         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue(element, element_compiler.get_computation())
         );
         init_instructions.add
         (
            BinarySearch.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(element),
               new Size(collection_compiler.get_ref()),
               collection_compiler.get_ref(),
               result_as_ref,
               index
            )
         );

         result_as_computation = new ValueOf(result_as_ref);
      }
      else
      {
         final Ref index, element;

         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         result_as_ref = reserve(Type.BOOLEAN);

         init_instructions.add
         (
            new SetValue(element, element_compiler.get_computation())
         );
         init_instructions.add
         (
            IterativeSearch.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(element),
               new Size(collection_compiler.get_ref()),
               collection_compiler.get_ref(),
               result_as_ref,
               index
            )
         );

         result_as_computation = new ValueOf(result_as_ref);
      }
   }

   @Override
   public void visit_macro_value_call
   (
      final tonkadur.fate.v1.lang.computation.MacroValueCall n
   )
   throws Throwable
   {
      final List<Ref> parameters;

      parameters = new ArrayList<Ref>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_computation:
            n.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         fate_computation.get_visited_by(cc);

         cc.generate_ref();

         assimilate(cc);

         parameters.add(cc.get_ref());
      }

      compiler.macros().push(n.get_macro(), parameters);
      n.get_actual_value_node().get_visited_by(this);
      compiler.macros().pop();
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
         final Ref candidate;
         final Computation value_of_candidate;

         candidate = reserve(operands.get(0).get_type());
         value_of_candidate = new ValueOf(candidate);

         init_instructions.add(new SetValue(candidate, operands.get(0)));

         for (final Computation operand: operands)
         {
            init_instructions.add
            (
               new SetValue
               (
                  candidate,
                  new IfElseComputation
                  (
                     Operation.less_than(value_of_candidate, operand),
                     value_of_candidate,
                     operand
                  )
               )
            );
         }

         result_as_computation = value_of_candidate;
      }
      else if
      (
         fate_op_name.equals
         (
            tonkadur.fate.v1.lang.computation.Operator.MAX.get_name()
         )
      )
      {
         final Ref candidate;
         final Computation value_of_candidate;

         candidate = reserve(operands.get(0).get_type());
         value_of_candidate = new ValueOf(candidate);

         init_instructions.add(new SetValue(candidate, operands.get(0)));

         for (final Computation operand: operands)
         {
            init_instructions.add
            (
               new SetValue
               (
                  candidate,
                  new IfElseComputation
                  (
                     Operation.greater_than(value_of_candidate, operand),
                     value_of_candidate,
                     operand
                  )
               )
            );
         }

         result_as_computation = value_of_candidate;
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

         if (operands.get(2).get_type().equals(Type.INT))
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
         final Ref candidate;
         final Computation value_of_candidate;

         t = operands.get(2).get_type();
         candidate = reserve(t);
         value_of_candidate = new ValueOf(candidate);

         init_instructions.add
         (
            new SetValue
            (
               candidate,
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
               candidate,
               new IfElseComputation
               (
                  Operation.less_than(value_of_candidate, operands.get(0)),
                  operands.get(0),
                  value_of_candidate
               )
            )
         );

         result_as_computation = value_of_candidate;
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
         result_as_computation =
            Operation.rand(operands.get(0), operands.get(1));
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
   public void visit_index_of_operator
   (
      final tonkadur.fate.v1.lang.computation.IndexOfOperator n
   )
   throws Throwable
   {
      /* TODO */
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
      final ComputationCompiler i_cc, n_cc;

      n_cc = new ComputationCompiler(compiler);
      i_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(n_cc);
      n.get_index().get_visited_by(i_cc);

      assimilate(n_cc);
      assimilate(i_cc);

      result_as_ref =
         new RelativeRef
         (
            n_cc.get_ref(),
            new Cast(i_cc.get_computation(), Type.STRING),
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
      /* TODO */
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
   public void visit_parameter_reference
   (
      final tonkadur.fate.v1.lang.computation.ParameterReference n
   )
   throws Throwable
   {
      result_as_ref = compiler.macros().get_parameter_ref(n.get_name());
   }

   @Override
   public void visit_ref_operator
   (
      final tonkadur.fate.v1.lang.computation.RefOperator n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_computation = n_cc.result_as_ref;
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
      n.get_value().get_visited_by(this);

      result_as_computation =
         new RichText(Collections.singletonList(get_computation()));
   }

   @Override
   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.computation.VariableReference n
   )
   throws Throwable
   {
      result_as_ref =
         compiler.world().get_variable(n.get_variable().get_name()).get_ref();

      result_as_computation = new ValueOf(result_as_ref);
   }
}
