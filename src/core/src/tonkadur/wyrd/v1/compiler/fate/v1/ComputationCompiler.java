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

import tonkadur.wyrd.v1.compiler.util.AddElement;
import tonkadur.wyrd.v1.compiler.util.AddElementsOf;
import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.CountOccurrences;
import tonkadur.wyrd.v1.compiler.util.CreateCons;
import tonkadur.wyrd.v1.compiler.util.FilterLambda;
import tonkadur.wyrd.v1.compiler.util.Fold;
import tonkadur.wyrd.v1.compiler.util.If;
import tonkadur.wyrd.v1.compiler.util.IfElse;
import tonkadur.wyrd.v1.compiler.util.IndexedFilterLambda;
import tonkadur.wyrd.v1.compiler.util.IndexedMapLambda;
import tonkadur.wyrd.v1.compiler.util.IndexedMergeLambda;
import tonkadur.wyrd.v1.compiler.util.IndexedPartitionLambda;
import tonkadur.wyrd.v1.compiler.util.InsertAt;
import tonkadur.wyrd.v1.compiler.util.IterativeSearch;
import tonkadur.wyrd.v1.compiler.util.LambdaEvaluation;
import tonkadur.wyrd.v1.compiler.util.MapLambda;
import tonkadur.wyrd.v1.compiler.util.MergeLambda;
import tonkadur.wyrd.v1.compiler.util.PartitionLambda;
import tonkadur.wyrd.v1.compiler.util.PopElement;
import tonkadur.wyrd.v1.compiler.util.RemoveAllOf;
import tonkadur.wyrd.v1.compiler.util.RemoveAt;
import tonkadur.wyrd.v1.compiler.util.RemoveElementsOf;
import tonkadur.wyrd.v1.compiler.util.RemoveOneOf;
import tonkadur.wyrd.v1.compiler.util.ReverseList;
import tonkadur.wyrd.v1.compiler.util.Shuffle;
import tonkadur.wyrd.v1.compiler.util.Sort;
import tonkadur.wyrd.v1.compiler.util.SubList;
import tonkadur.wyrd.v1.compiler.util.While;

import tonkadur.wyrd.v1.lang.computation.*;

import tonkadur.wyrd.v1.lang.World;
import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

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

   protected void assimilate_reserved_registers (final ComputationCompiler cc)
   {
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
   public void visit_field_access
   (
      final tonkadur.fate.v1.lang.computation.FieldAccess n
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
                  (tonkadur.fate.v1.lang.type.StructType)
                     n.get_parent().get_type()
               ).get_field_type(null, n.get_field_name())
            )
         );
   }

   @Override
   public void visit_default
   (
      final tonkadur.fate.v1.lang.computation.Default n
   )
   throws Throwable
   {
      final Register r;

      r = reserve(TypeCompiler.compile(compiler, n.get_type()));

      init_instructions.add(new Initialize(r.get_address()));

      result_as_address = r.get_address();
      result_as_computation = r.get_value();
   }

/*
   @Override
   public void visit_access_pointer
   (
      final tonkadur.fate.v1.lang.computation.AccessPointer n
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

      result_as_computation =
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
*/
/*
   @Override
   public void visit_access_as_reference
   (
      final tonkadur.fate.v1.lang.computation.AccessAsReference n
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
*/
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

      result_as_computation = new Text(content);
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
         new AddTextEffect
         (
            n.get_effect().get_name(),
            parameters,
            Collections.singletonList(text_cc.get_computation())
         );
   }

   @Override
   public void visit_value_to_text
   (
      final tonkadur.fate.v1.lang.computation.ValueToText n
   )
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new ComputationCompiler(compiler);

      n.get_value().get_visited_by(cc);

      assimilate(cc);

      result_as_computation =
         new Text(Collections.singletonList(cc.get_computation()));
   }

   @Override
   public void visit_lambda_expression
   (
      final tonkadur.fate.v1.lang.computation.LambdaExpression n
   )
   throws Throwable
   {
      final String out_label, in_label;
      final List<Register> side_channel_parameters;
      final List<Register> parameters;
      final Register result_holder;
      final ComputationCompiler expr_compiler;
      final Type result_type;
      final String context_name;
      final Register result, lambda_data_register;
      final Address result_line_address;
      int i;

      out_label = compiler.assembler().generate_label("<lambda_expr#out>");
      in_label = compiler.assembler().generate_label("<lambda_expr#in>");

      parameters = new ArrayList<Register>();
      side_channel_parameters = new ArrayList<Register>();

      result = reserve(DictType.WILD);

      result_line_address =
         new RelativeAddress
         (
            result.get_address(),
            Constant.string_value("l"),
            Type.INT
         );

      init_instructions.add
      (
         new Initialize(result_line_address, Type.INT)
      );

      init_instructions.add
      (
         new SetValue
         (
            result_line_address,
            compiler.assembler().get_label_constant(in_label)
         )
      );

      result_as_computation = result.get_value();
      result_as_address = result.get_address();

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

      lambda_data_register =
         compiler.registers().reserve
         (
            DictType.WILD,
            init_instructions
         );

      side_channel_parameters.add(result_holder);
      side_channel_parameters.add(lambda_data_register);

      init_instructions.addAll
      (
         compiler.registers().read_parameters(side_channel_parameters)
      );

      i = n.get_parameters().size() - 1;

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
         init_instructions.add
         (
            new SetValue
            (
               r.get_address(),
               new ValueOf
               (
                  new RelativeAddress
                  (
                     new Address
                     (
                        lambda_data_register.get_value(),
                        DictType.WILD
                     ),
                     Constant.string_value(Integer.toString(i)),
                     r.get_value().get_type()
                  )
               )
            )
         );

         --i;
      }

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

   }

   @Override
   public void visit_generic_computation
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation n
   )
   throws Throwable
   {
      final ComputationCompiler handler;

      handler = GenericComputationCompiler.handle(compiler, n);

      assimilate(handler);

      this.instructions_were_generated = handler.instructions_were_generated;
      this.result_as_computation = handler.result_as_computation;
      this.result_as_address = handler.result_as_address;
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

   @Override
   public void visit_sequence_reference
   (
      final tonkadur.fate.v1.lang.computation.SequenceReference n
   )
   throws Throwable
   {
      result_as_computation =
         compiler.assembler().get_label_constant
         (
            n.get_sequence_name()
         );
   }
}
