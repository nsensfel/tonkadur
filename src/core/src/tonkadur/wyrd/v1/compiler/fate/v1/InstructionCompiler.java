package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.error.Error;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.World;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.RelativeRef;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.AddChoice;
import tonkadur.wyrd.v1.lang.instruction.Assert;
import tonkadur.wyrd.v1.lang.instruction.Display;
import tonkadur.wyrd.v1.lang.instruction.End;
import tonkadur.wyrd.v1.lang.instruction.EventCall;
import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.ResolveChoices;
import tonkadur.wyrd.v1.lang.instruction.SetPC;
import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.AnonymousVariableManager;
import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.InsertAt;
import tonkadur.wyrd.v1.compiler.util.If;
import tonkadur.wyrd.v1.compiler.util.IfElse;
import tonkadur.wyrd.v1.compiler.util.InstructionManager;
import tonkadur.wyrd.v1.compiler.util.NOP;
import tonkadur.wyrd.v1.compiler.util.While;
import tonkadur.wyrd.v1.compiler.util.Clear;
import tonkadur.wyrd.v1.compiler.util.IterativeSearch;
import tonkadur.wyrd.v1.compiler.util.RemoveAllOf;
import tonkadur.wyrd.v1.compiler.util.ReverseList;
import tonkadur.wyrd.v1.compiler.util.RemoveAt;

public class InstructionCompiler
implements tonkadur.fate.v1.lang.meta.InstructionVisitor
{
   protected final Compiler compiler;
   protected final List<Instruction> result;

   public InstructionCompiler (final Compiler compiler)
   {
      this.compiler = compiler;
      result = new ArrayList<Instruction>();
   }

   protected Instruction get_result ()
   {
      return compiler.assembler().merge(result);
   }

   protected void add_element_to_set
   (
      final tonkadur.fate.v1.lang.instruction.AddElement ae
   )
   throws Throwable
   {
      /*
       * Fate:
       * (add_element element collection)
       *
       * Wyrd:
       * (declare_variable local <element.type> .anon0)
       * (set .anon0 element)
       * (declare_variable local boolean .found)
       * <binary_search .anon0 collection .found .index>
       * (ifelse (var .found)
       *    (nop)
       *    <insert_at ...>
       * )
       */
      final ComputationCompiler element_compiler, reference_compiler;
      final Ref element, collection, collection_size;
      final Ref element_found, element_index;
      final Type element_type;
      final Computation value_of_element, value_of_collection_size;

      element_compiler = new ComputationCompiler(compiler);
      ae.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      element_type = element_compiler.get_computation().get_type();
      element = compiler.anonymous_variables().reserve(element_type);
      result.add(new SetValue(element, element_compiler.get_computation()));
      element_compiler.release_variables();

      reference_compiler = new ComputationCompiler(compiler);
      ae.get_collection().get_visited_by(reference_compiler);

      if (reference_compiler.has_init())
      {
         result.add(reference_compiler.get_init());
      }

      collection = reference_compiler.get_ref();

      element_found = compiler.anonymous_variables().reserve(Type.BOOLEAN);
      element_index = compiler.anonymous_variables().reserve(Type.INT);
      collection_size = compiler.anonymous_variables().reserve(Type.INT);

      value_of_element = new ValueOf(element);
      value_of_collection_size = new ValueOf(collection_size);

      result.add
      (
         new SetValue(collection_size, new Size(collection))
      );

      result.add
      (
         BinarySearch.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            value_of_element,
            value_of_collection_size,
            collection,
            element_found,
            element_index
         )
      );

      result.add
      (
         If.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            Operation.not(new ValueOf(element_found)),
            InsertAt.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               element_index,
               value_of_element,
               value_of_collection_size,
               collection
            )
         )
      );

      compiler.anonymous_variables().release(element);
      compiler.anonymous_variables().release(element_found);
      compiler.anonymous_variables().release(element_index);
      compiler.anonymous_variables().release(collection_size);

      reference_compiler.release_variables();
   }

   protected void add_element_to_list
   (
      final tonkadur.fate.v1.lang.instruction.AddElement ae
   )
   throws Throwable
   {
      /*
       * Fate:
       * (add_element element collection)
       *
       * Wyrd:
       * (set
       *    (relative_ref collection ( (cast int string (size collection)) ))
       *    (element)
       * )
       */
      final Ref collection_as_ref;
      final ComputationCompiler element_compiler, reference_compiler;

      element_compiler = new ComputationCompiler(compiler);

      ae.get_element().get_visited_by(element_compiler);

      reference_compiler = new ComputationCompiler(compiler);

      ae.get_collection().get_visited_by(reference_compiler);


      if (reference_compiler.has_init())
      {
         result.add(reference_compiler.get_init());
      }

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      collection_as_ref = reference_compiler.get_ref();

      result.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection_as_ref,
               new Cast
               (
                  new Size(collection_as_ref),
                  Type.STRING
               ),
               element_compiler.get_computation().get_type()
            ),
            element_compiler.get_computation()
         )
      );

      reference_compiler.release_variables();
      element_compiler.release_variables();
   }

   @Override
   public void visit_add_element
   (
      final tonkadur.fate.v1.lang.instruction.AddElement ae
   )
   throws Throwable
   {
      final tonkadur.fate.v1.lang.type.Type collection_type;
      final tonkadur.fate.v1.lang.type.CollectionType collection_true_type;

      collection_type = ae.get_collection().get_type();

      if
      (
         !(collection_type instanceof tonkadur.fate.v1.lang.type.CollectionType)
      )
      {
         System.err.println
         (
            "[P] (add_element item collection), but this is not a collection: "
            + ae.get_collection()
            + ". It's a "
            + collection_type.get_name()
            + "."
         );
      }

      collection_true_type =
         (tonkadur.fate.v1.lang.type.CollectionType) collection_type;

      if (collection_true_type.is_set())
      {
         add_element_to_set(ae);
      }
      else
      {
         add_element_to_list(ae);
      }
   }

   @Override
   public void visit_assert (final tonkadur.fate.v1.lang.instruction.Assert a)
   throws Throwable
   {
      /*
       * Fate: (assert Computation)
       *
       * Wyrd: (assert Computation)
       */
      final ComputationCompiler cond_cc, msg_cc;

      cond_cc = new ComputationCompiler(compiler);
      msg_cc = new ComputationCompiler(compiler);

      a.get_condition().get_visited_by(cond_cc);
      a.get_message().get_visited_by(msg_cc);

      if (cond_cc.has_init())
      {
         result.add(cond_cc.get_init());
      }

      if (msg_cc.has_init())
      {
         result.add(msg_cc.get_init());
      }

      result.add
      (
         new Assert(cond_cc.get_computation(), msg_cc.get_computation())
      );

      cond_cc.release_variables();
      msg_cc.release_variables();
   }

   @Override
   public void visit_clear (final tonkadur.fate.v1.lang.instruction.Clear c)
   throws Throwable
   {
      /*
       * Fate: (clear collection)
       *
       * Wyrd: <clear collection>
       */
      final ComputationCompiler reference_compiler;
      final Ref collection_ref;

      reference_compiler = new ComputationCompiler(compiler);

      c.get_collection().get_visited_by(reference_compiler);

      if (reference_compiler.has_init())
      {
         result.add(reference_compiler.get_init());
      }

      collection_ref = reference_compiler.get_ref();

      result.add
      (
         Clear.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            new Size(collection_ref),
            collection_ref
         )
      );

      reference_compiler.release_variables();
   }

   public void visit_reverse_list
   (
      final tonkadur.fate.v1.lang.instruction.ReverseList n
   )
   throws Throwable
   {
      /*
       * Fate: (reverse_list collection)
       *
       * Wyrd: <reverse_list collection>
       */
      final ComputationCompiler reference_compiler;
      final Ref collection_ref;

      reference_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(reference_compiler);

      if (reference_compiler.has_init())
      {
         result.add(reference_compiler.get_init());
      }

      collection_ref = reference_compiler.get_ref();

      result.add
      (
         ReverseList.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            new Size(collection_ref),
            collection_ref
         )
      );

      reference_compiler.release_variables();
   }

   @Override
   public void visit_switch_instruction
   (
      final tonkadur.fate.v1.lang.instruction.SwitchInstruction n
   )
   throws Throwable
   {
      /*
       * Fate:
       *    (switch target
       *       (c0 i0)
       *       (... ...)
       *       (cn in)
       *       default
       *    )
       *
       * Wyrd:
       *    (declare_variable <target.type> .anon)
       *
       *    (set .anon target) ;; in case target requires computation.
       *
       *    <ifelse (= c0 .anon)
       *       i0
       *       <ifelse ...
       *          ...
       *          <ifelse (= cn .anon)
       *             in
       *             default
       *          >
       *       >
       *    >
       */
      Ref anon;
      final List
      <
         Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Instruction
         >
      > branches;
      InstructionCompiler ic;
      ComputationCompiler target_cc, cc;
      Computation value_of_anon;
      List<Instruction> current_branch, previous_else_branch;

      branches = new ArrayList<>(n.get_branches()); // shallow copy.

      Collections.reverse(branches);

      previous_else_branch = new ArrayList<Instruction>();

      ic = new InstructionCompiler(compiler);
      n.get_default_instruction().get_visited_by(ic);

      previous_else_branch.add(ic.get_result());

      target_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      target_cc.generate_ref();

      if (target_cc.has_init())
      {
         result.add(target_cc.get_init());
      }

      anon = target_cc.get_ref();
      value_of_anon = new ValueOf(anon);

      for
      (
         final
         Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Instruction
         >
         branch:
         branches
      )
      {
         current_branch = new ArrayList<Instruction>();

         ic = new InstructionCompiler(compiler);
         cc = new ComputationCompiler(compiler);

         branch.get_car().get_visited_by(cc);
         branch.get_cdr().get_visited_by(ic);

         if (cc.has_init())
         {
            current_branch.add(cc.get_init());
         }

         current_branch.add
         (
            IfElse.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               Operation.equals(value_of_anon, cc.get_computation()),
               ic.get_result(),
               compiler.assembler().merge(previous_else_branch)
            )
         );

         previous_else_branch = current_branch;

         cc.release_variables();
      }

      result.add(compiler.assembler().merge(previous_else_branch));
   }

   @Override
   public void visit_display (final tonkadur.fate.v1.lang.instruction.Display n)
   throws Throwable
   {
      /*
       * Fate: (display Computation)
       *
       * Wyrd: (display Computation)
       */
      final ComputationCompiler cc;

      cc = new ComputationCompiler(compiler);

      n.get_content().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      result.add(new Display(cc.get_computation()));

      cc.release_variables();
   }

   @Override
   public void visit_free (final tonkadur.fate.v1.lang.instruction.Free n)
   throws Throwable
   {
      final ComputationCompiler cc;
      final Ref target;

      cc = new ComputationCompiler(compiler);

      n.get_reference().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      target = cc.get_ref();

      if (target == null)
      {
         System.err.println
         (
            "[P] Argument in (free "
            + n.get_reference()
            + ") did not compile to a reference."
         );
      }

      result.add(new Remove(target));

      cc.release_variables();
   }

   @Override
   public void visit_while (final tonkadur.fate.v1.lang.instruction.While n)
   throws Throwable
   {
      final ComputationCompiler cc;
      final List<Instruction> body;
      final String end_of_loop_label;
      InstructionCompiler ic;

      end_of_loop_label =
         compiler.assembler().generate_label("<AfterWhile>");

      compiler.assembler().push_context_label("breakable", end_of_loop_label);

      cc = new ComputationCompiler(compiler);
      body = new ArrayList<Instruction>();

      n.get_condition().get_visited_by(cc);

      for (final tonkadur.fate.v1.lang.meta.Instruction i: n.get_body())
      {
         ic = new InstructionCompiler(compiler);
         i.get_visited_by(ic);

         body.add(ic.get_result());
      }


      if (cc.has_init())
      {
         result.add
         (
            compiler.assembler().mark_after
            (
               While.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler(),
                  cc.get_init(),
                  cc.get_computation(),
                  compiler.assembler().merge(body)
               ),
               end_of_loop_label
            )
         );
      }
      else
      {
         result.add
         (
            compiler.assembler().mark_after
            (
               While.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler(),
                  cc.get_computation(),
                  compiler.assembler().merge(body)
               ),
               end_of_loop_label
            )
         );
      }

      compiler.assembler().pop_context_label("breakable");
      cc.release_variables();
   }

   @Override
   public void visit_do_while (final tonkadur.fate.v1.lang.instruction.DoWhile n)
   throws Throwable
   {
      final List<Instruction> pre_cond_instructions;
      final ComputationCompiler cc;
      final String end_of_loop_label;
      InstructionCompiler ic;

      end_of_loop_label =
         compiler.assembler().generate_label("<AfterDoWhile>");

      compiler.assembler().push_context_label("breakable", end_of_loop_label);

      pre_cond_instructions = new ArrayList<Instruction>();

      cc = new ComputationCompiler(compiler);
      n.get_condition().get_visited_by(cc);

      for (final tonkadur.fate.v1.lang.meta.Instruction i: n.get_body())
      {
         ic = new InstructionCompiler(compiler);
         i.get_visited_by(ic);

         pre_cond_instructions.add(ic.get_result());
      }

      if (cc.has_init())
      {
         pre_cond_instructions.add(cc.get_init());
      }

      result.add
      (
         compiler.assembler().mark_after
         (
            While.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               compiler.assembler().merge(pre_cond_instructions),
               cc.get_computation(),
               NOP.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler()
               )
            ),
            end_of_loop_label
         )
      );

      compiler.assembler().pop_context_label("breakable");
      cc.release_variables();
   }

   @Override
   public void visit_for (final tonkadur.fate.v1.lang.instruction.For n)
   throws Throwable
   {
      final ComputationCompiler cc;
      final List<Instruction> body;
      final String end_of_loop_label;
      InstructionCompiler ic;

      end_of_loop_label =
         compiler.assembler().generate_label("<AfterFor>");

      compiler.assembler().push_context_label("breakable", end_of_loop_label);

      body = new ArrayList<Instruction>();

      ic = new InstructionCompiler(compiler);
      n.get_pre().get_visited_by(ic);

      result.add(ic.get_result());

      for (final tonkadur.fate.v1.lang.meta.Instruction i: n.get_body())
      {
         ic = new InstructionCompiler(compiler);
         i.get_visited_by(ic);

         body.add(ic.get_result());
      }

      ic = new InstructionCompiler(compiler);
      n.get_post().get_visited_by(ic);

      body.add(ic.get_result());

      cc = new ComputationCompiler(compiler);
      n.get_condition().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add
         (
            compiler.assembler().mark_after
            (
               While.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler(),
                  cc.get_init(),
                  cc.get_computation(),
                  compiler.assembler().merge(body)
               ),
               end_of_loop_label
            )
         );
      }
      else
      {
         result.add
         (
            compiler.assembler().mark_after
            (
               While.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler(),
                  cc.get_computation(),
                  compiler.assembler().merge(body)
               ),
               end_of_loop_label
            )
         );
      }

      compiler.assembler().pop_context_label("breakable");
      cc.release_variables();
   }

   @Override
   public void visit_for_each (final tonkadur.fate.v1.lang.instruction.ForEach n)
   throws Throwable
   {
      final String end_of_loop_label;
      final ComputationCompiler cc;
      final List<Instruction> new_body;
      final Ref index, current_value, collection_size;
      final Ref collection;
      final Computation value_of_index;
      final Type member_type;

      /*
       * (declare_variable int index)
       * (declare_variable int collection_size)
       *
       * (set index 0)
       * (set collection_size (size collection))
       *
       * (declare_variable <E> current_value)
       * <add_wild_parameter current_value>
       * ...
       */
      cc = new ComputationCompiler(compiler);
      new_body = new ArrayList<Instruction>();

      index = compiler.anonymous_variables().reserve(Type.INT);
      collection_size = compiler.anonymous_variables().reserve(Type.INT);

      result.add(new SetValue(index, Constant.ZERO));

      n.get_collection().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      collection = cc.get_ref();

      result.add(new SetValue(collection_size, new Size(collection)));

      value_of_index = new ValueOf(index);

      member_type = ((MapType) collection.get_target_type()).get_member_type();

      current_value = compiler.anonymous_variables().reserve(member_type);

      end_of_loop_label = compiler.assembler().generate_label("<AfterForEach>");

      compiler.assembler().push_context_label("breakable", end_of_loop_label);
      compiler.macros().add_wild_parameter
      (
         n.get_parameter_name(),
         current_value
      );

      new_body.add
      (
         new SetValue
         (
            current_value,
            new ValueOf
            (
               new RelativeRef
               (
                  collection,
                  new Cast(value_of_index, Type.STRING),
                  member_type
               )
            )
         )
      );

      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instr: n.get_body()
      )
      {
         final InstructionCompiler ic;

         ic = new InstructionCompiler(compiler);

         fate_instr.get_visited_by(ic);

         new_body.add(ic.get_result());
      }

      new_body.add
      (
         new SetValue(index, Operation.plus(Constant.ONE, value_of_index))
      );

      result.add
      (
         compiler.assembler().mark_after
         (
            While.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               Operation.less_than
               (
                  value_of_index,
                  new ValueOf(collection_size)
               ),
               compiler.assembler.merge(new_body)
            ),
            end_of_loop_label
         )
      );
      compiler.macros().remove_wild_parameter(n.get_parameter_name());
      compiler.assembler().pop_context_label("breakable");

      compiler.anonymous_variables().release(index);
      compiler.anonymous_variables().release(current_value);
      compiler.anonymous_variables().release(collection_size);
   }

   @Override
   public void visit_remove_element_at
   (
      final tonkadur.fate.v1.lang.instruction.RemoveElementAt n
   )
   throws Throwable
   {
      final ComputationCompiler index_cc, collection_cc;
      final Ref collection, collection_size;
      final Computation value_of_collection_size;

      index_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.anonymous_variables().reserve(Type.INT);

      n.get_index().get_visited_by(index_cc);
      n.get_collection().get_visited_by(collection_cc);

      index_cc.generate_ref();

      if (index_cc.has_init())
      {
         result.add(index_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection = collection_cc.get_ref();

      value_of_collection_size = new ValueOf(collection_size);

      result.add(new SetValue(collection_size, new Size(collection)));

      result.add
      (
         RemoveAt.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            index_cc.get_ref(),
            value_of_collection_size,
            collection
         )
      );

      compiler.anonymous_variables().release(collection_size);

      index_cc.release_variables();
      collection_cc.release_variables();
   }


   @Override
   public void visit_cond_instruction
   (
      final tonkadur.fate.v1.lang.instruction.CondInstruction ci
   )
   throws Throwable
   {
      /*
       * Fate:
       *    (cond
       *       (c0 i0)
       *       (... ...)
       *       (cn in)
       *    )
       *
       * Wyrd:
       *    <ifelse c0
       *       i0
       *       <ifelse ...
       *          ...
       *          <ifelse cn
       *             in
       *             <nop>
       *          >
       *       >
       *    >
       */
      final List
      <
         Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Instruction
         >
      > branches;
      InstructionCompiler ic;
      ComputationCompiler cc;
      List<Instruction> previous_else_branch;
      List<Instruction> current_branch;

      branches = new ArrayList(ci.get_branches()); // shallow copy
      previous_else_branch = new ArrayList<Instruction>();

      Collections.reverse(branches);

      previous_else_branch.add
      (
         NOP.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler()
         )
      );

      for
      (
         final
         Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Instruction
         >
         branch:
            branches
      )
      {
         current_branch = new ArrayList<Instruction>();

         ic = new InstructionCompiler(compiler);
         cc = new ComputationCompiler(compiler);

         branch.get_car().get_visited_by(cc);
         branch.get_cdr().get_visited_by(ic);

         if (cc.has_init())
         {
            current_branch.add(cc.get_init());
         }

         current_branch.add
         (
            IfElse.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               cc.get_computation(),
               ic.get_result(),
               compiler.assembler().merge(previous_else_branch)
            )
         );

         previous_else_branch = current_branch;

         cc.release_variables();
      }

      result.add(compiler.assembler().merge(previous_else_branch));
   }


   @Override
   public void visit_event_call
   (
      final tonkadur.fate.v1.lang.instruction.EventCall n
   )
   throws Throwable
   {
      /*
       * Fate: (event_call <string> c0 ... cn)
       *
       * Wyrd (event_call <string> c0 ... cn)
       */
      final List<ComputationCompiler> cc_list;
      final List<Computation> parameters;

      cc_list = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_computation:
            n.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         fate_computation.get_visited_by(cc);

         if (cc.has_init())
         {
            result.add(cc.get_init());
         }

         cc_list.add(cc);
         parameters.add(cc.get_computation());
      }

      result.add(new EventCall(n.get_event().get_name(), parameters));

      for (final ComputationCompiler cc: cc_list)
      {
         cc.release_variables();
      }
   }

   @Override
   public void visit_if_else_instruction
   (
      final tonkadur.fate.v1.lang.instruction.IfElseInstruction n
   )
   throws Throwable
   {
      /*
       * Fate: (ifelse c0 i0 i1)
       *
       * Wyrd (ifelse c0 i0 i1)
       */
      final ComputationCompiler cc;
      final InstructionCompiler if_true_ic;
      final InstructionCompiler if_false_ic;

      cc = new ComputationCompiler(compiler);
      if_true_ic = new InstructionCompiler(compiler);
      if_false_ic = new InstructionCompiler(compiler);

      n.get_condition().get_visited_by(cc);
      n.get_if_true().get_visited_by(if_true_ic);
      n.get_if_false().get_visited_by(if_false_ic);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      result.add
      (
         IfElse.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            cc.get_computation(),
            if_true_ic.get_result(),
            if_false_ic.get_result()
         )
      );

      cc.release_variables();
   }

   @Override
   public void visit_if_instruction
   (
      final tonkadur.fate.v1.lang.instruction.IfInstruction n
   )
   throws Throwable
   {
      /*
       * Fate: (ifelse c0 i0 i1)
       *
       * Wyrd (ifelse c0 i0 (nop))
       */
      final ComputationCompiler cc;
      final InstructionCompiler if_true_ic;

      cc = new ComputationCompiler(compiler);
      if_true_ic = new InstructionCompiler(compiler);

      n.get_condition().get_visited_by(cc);
      n.get_if_true().get_visited_by(if_true_ic);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      result.add
      (
         If.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            cc.get_computation(),
            if_true_ic.get_result()
         )
      );

      cc.release_variables();
   }

   @Override
   public void visit_instruction_list
   (
      final tonkadur.fate.v1.lang.instruction.InstructionList n
   )
   throws Throwable
   {
      /*
       * Fate: i0 ... in
       *
       * Wyrd i0 ... in
       */
      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_instructions()
      )
      {
         final InstructionCompiler ic;

         ic = new InstructionCompiler(compiler);

         fate_instruction.get_visited_by(ic);

         result.add(ic.get_result());
      }
   }

   @Override
   public void visit_macro_call
   (
      final tonkadur.fate.v1.lang.instruction.MacroCall n
   )
   throws Throwable
   {
      /*
       * Fate: (macro <string> c0 ... cn)
       *
       * Wyrd <content of macro with c0 ... cn>
       */
      final List<ComputationCompiler> cc_list;
      final List<Ref> parameters;

      cc_list = new ArrayList<ComputationCompiler>();
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

         if (cc.has_init())
         {
            result.add(cc.get_init());
         }

         cc_list.add(cc);
         parameters.add(cc.get_ref());
      }

      compiler.macros().push(n.get_macro(), parameters);
      n.get_macro().get_root().get_visited_by(this);
      compiler.macros().pop();

      for (final ComputationCompiler cc: cc_list)
      {
         cc.release_variables();
      }
   }

   @Override
   public void visit_end
   (
      final tonkadur.fate.v1.lang.instruction.End n
   )
   throws Throwable
   {
      result.add(new End());
   }

   @Override
   public void visit_break
   (
      final tonkadur.fate.v1.lang.instruction.Break n
   )
   throws Throwable
   {
      result.add
      (
         new SetPC
         (
            compiler.assembler().get_context_label_constant("breakable")
         )
      );
   }

   @Override
   public void visit_player_choice
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoice n
   )
   throws Throwable
   {
      /*
       * Fate: (player_choice label i0)
       *
       * Wyrd (add_choice label i0)
       */
      final ComputationCompiler cc;
      final String start_of_effect, end_of_effect;

      cc = new ComputationCompiler(compiler);

      start_of_effect = compiler.assembler().generate_label("<choice#start>");
      end_of_effect = compiler.assembler().generate_label("<choice#end>");

      n.get_text().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      result.add
      (
         new AddChoice
         (
            cc.get_computation(),
            compiler.assembler().get_label_constant(start_of_effect)
         )
      );
      result.add
      (
         compiler.assembler().mark_after
         (
            new SetPC
            (
               compiler.assembler().get_label_constant(end_of_effect)
            ),
            start_of_effect
         )
      );

      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_effects()
      )
      {
         fate_instruction.get_visited_by(this);
      }

      result.add
      (
         compiler.assembler().mark_after
         (
            new SetPC
            (
               compiler.assembler().get_context_label_constant("choices")
            ),
            end_of_effect
         )
      );

      cc.release_variables();
   }

   @Override
   public void visit_player_choice_list
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoiceList n
   )
   throws Throwable
   {
      final String end_of_choices_label;

      end_of_choices_label =
         compiler.assembler().generate_label("<ChoicesResolved>");

      compiler.assembler().push_context_label("choices", end_of_choices_label);

      /*
       * Fate: (player_choice_list i0 ... in)
       *
       * Wyrd:
       *    i0
       *    ...
       *    in
       *    (resolve_choices)
       */
      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_choices()
      )
      {
         fate_instruction.get_visited_by(this);
      }

      compiler.assembler().pop_context_label("choices");

      result.add
      (
         compiler.assembler().mark_after
         (
            new ResolveChoices(),
            end_of_choices_label
         )
      );
   }

   @Override
   public void visit_remove_all_of_element
   (
      final tonkadur.fate.v1.lang.instruction.RemoveAllOfElement n
   )
   throws Throwable
   {
      /*
       * Fate:
       * (remove_all_of element collection)
       *
       * Wyrd:
       * (declare_variable <element_type> .elem)
       * (declare_variable int .collection_size)
       *
       * (set .elem element)
       * (set .collection_size (size collection))
       *
       * <if collection is a list:
       *    <remove_all (var .elem) (var .collection_size) collection>
       * >
       * <if collection is a set:
       *    (declare_variable bool .found)
       *    (declare_variable int .index)
       *
       *    <binary_search
       *       (var .elem)
       *       (var .collection_size)
       *       collection
       *       .found
       *       .index
       *    >
       *    (ifelse (var .found)
       *       <remove_at (var .index) (var .collection_size) collection>
       *       (nop)
       *    )
       * >
       */
      final ComputationCompiler elem_cc, collection_cc;
      final Ref elem, collection_size, collection;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.anonymous_variables().reserve(Type.INT);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      elem =
         compiler.anonymous_variables().reserve
         (
            elem_cc.get_computation().get_type()
         );


      if (elem_cc.has_init())
      {
         result.add(elem_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection = collection_cc.get_ref();

      result.add(new SetValue(elem, elem_cc.get_computation()));
      result.add(new SetValue(collection_size, new Size(collection)));

      elem_cc.release_variables();

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
            n.get_collection().get_type()
         ).is_set()
      )
      {
         final Computation value_of_elem, value_of_collection_size;
         final Ref index, found;

         index = compiler.anonymous_variables().reserve(Type.INT);
         found = compiler.anonymous_variables().reserve(Type.BOOLEAN);

         value_of_elem = new ValueOf(elem);
         value_of_collection_size = new ValueOf(collection_size);

         result.add
         (
            BinarySearch.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               value_of_elem,
               value_of_collection_size,
               collection,
               found,
               index
            )
         );

         result.add
         (
            If.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(found),
               RemoveAt.generate
               (
                  compiler.anonymous_variables(),
                  compiler.assembler(),
                  index,
                  value_of_collection_size,
                  collection
               )
            )
         );

         compiler.anonymous_variables().release(index);
         compiler.anonymous_variables().release(found);
      }
      else
      {
         result.add
         (
            RemoveAllOf.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(elem),
               new ValueOf(collection_size),
               collection
            )
         );
      }

      collection_cc.release_variables();

      compiler.anonymous_variables().release(elem);
      compiler.anonymous_variables().release(collection_size);
   }

   @Override
   public void visit_remove_element
   (
      final tonkadur.fate.v1.lang.instruction.RemoveElement n
   )
   throws Throwable
   {
      /*
       * Fate:
       * (remove_element element collection)
       *
       * Wyrd:
       * (declare_variable <element_type> .elem)
       * (declare_variable int .collection_size)
       * (declare_variable boolean .found)
       * (declare_variable int .index)
       *
       * (set .elem element)
       * (set .collection_size (size collection))
       *
       * <if collection is a set:
       *    <BinarySearch
       *       (var .elem)
       *       (var .collection_size)
       *       collection
       *       .found
       *       .index
       *    >
       * >
       * <if collection is a list:
       *    <IterativeSearch
       *       (var .elem)
       *       (var .collection_size)
       *       collection
       *       .found
       *       .index
       *    >
       * >
       *
       * (if (var .found)
       *    <remove_at (var index) (var .collection_size) collection>
       *    (nop)
       * )
       */
      final ComputationCompiler elem_cc, collection_cc;
      final Ref elem, collection_size, found, index;
      final Ref collection;
      final Computation value_of_collection_size;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.anonymous_variables().reserve(Type.INT);
      found = compiler.anonymous_variables().reserve(Type.BOOLEAN);
      index = compiler.anonymous_variables().reserve(Type.INT);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      elem =
         compiler.anonymous_variables().reserve
         (
            elem_cc.get_computation().get_type()
         );


      if (elem_cc.has_init())
      {
         result.add(elem_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection = collection_cc.get_ref();

      value_of_collection_size = new ValueOf(collection_size);

      result.add(new SetValue(elem, elem_cc.get_computation()));
      result.add(new SetValue(collection_size, new Size(collection)));

      elem_cc.release_variables();

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
            n.get_collection().get_type()
         ).is_set()
      )
      {
         result.add
         (
            BinarySearch.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(elem),
               value_of_collection_size,
               collection,
               found,
               index
            )
         );
      }
      else
      {
         result.add
         (
            IterativeSearch.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               new ValueOf(elem),
               value_of_collection_size,
               collection,
               found,
               index
            )
         );
      }

      compiler.anonymous_variables().release(elem);

      result.add
      (
         If.generate
         (
            compiler.anonymous_variables(),
            compiler.assembler(),
            new ValueOf(found),
            RemoveAt.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               index,
               value_of_collection_size,
               collection
            )
         )
      );

      compiler.anonymous_variables().release(index);
      compiler.anonymous_variables().release(found);
      compiler.anonymous_variables().release(collection_size);

      collection_cc.release_variables();
   }

   @Override
   public void visit_sequence_call
   (
      final tonkadur.fate.v1.lang.instruction.SequenceCall n
   )
   throws Throwable
   {
      /*
       * Fate: (sequence_call string)
       * Wyrd: (set_pc <label string>)
       */
      compiler.assembler().add_fixed_name_label(n.get_sequence_name());

      result.add
      (
         new SetPC
         (
            compiler.assembler().get_label_constant(n.get_sequence_name())
         )
      );
   }

   @Override
   public void visit_set_value
   (
      final tonkadur.fate.v1.lang.instruction.SetValue n
   )
   throws Throwable
   {
      /*
       * Fate: (set_value ref value)
       * Wyrd: (set_value ref value)
       */
      final ComputationCompiler value_cc, ref_cc;

      value_cc = new ComputationCompiler(compiler);
      ref_cc = new ComputationCompiler(compiler);

      n.get_value().get_visited_by(value_cc);

      if (value_cc.has_init())
      {
         result.add(value_cc.get_init());
      }

      n.get_reference().get_visited_by(ref_cc);

      if (ref_cc.has_init())
      {
         result.add(ref_cc.get_init());
      }

      result.add
      (
         new SetValue(ref_cc.get_ref(), value_cc.get_computation())
      );

      value_cc.release_variables();
      ref_cc.release_variables();
   }
}
