package tonkadur.wyrd.v1.compiler.fate.v1;

/* TODO: clean this up, way too many `new ...` could be reused. */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.error.Error;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.World;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;

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
import tonkadur.wyrd.v1.lang.instruction.EventCall;
import tonkadur.wyrd.v1.lang.instruction.IfElseInstruction;
import tonkadur.wyrd.v1.lang.instruction.NOP;
import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.ResolveChoices;
import tonkadur.wyrd.v1.lang.instruction.SequenceCall;
import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.While;

import tonkadur.wyrd.v1.compiler.util.AnonymousVariableManager;
import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.InsertAt;
import tonkadur.wyrd.v1.compiler.util.IterativeSearch;
import tonkadur.wyrd.v1.compiler.util.RemoveAllOf;
import tonkadur.wyrd.v1.compiler.util.RemoveAt;

public class InstructionCompiler
implements tonkadur.fate.v1.lang.meta.InstructionVisitor
{
   protected final AnonymousVariableManager anonymous_variables;
   protected final World wyrd_world;
   protected final MacroManager macro_manager;
   protected final List<Instruction> result;

   public InstructionCompiler
   (
      final MacroManager macro_manager,
      final AnonymousVariableManager anonymous_variables,
      final World wyrd_world
   )
   {
      this.macro_manager = macro_manager;
      this.anonymous_variables = anonymous_variables;
      this.wyrd_world = wyrd_world;
      result = new ArrayList<Instruction>();
   }

   public InstructionCompiler
   (
      final MacroManager macro_manager,
      final AnonymousVariableManager anonymous_variables,
      final World wyrd_world,
      final List<Instruction> result
   )
   {
      this.macro_manager = macro_manager;
      this.anonymous_variables = anonymous_variables;
      this.wyrd_world = wyrd_world;
      this.result = result;
   }

   protected List<Instruction> get_result ()
   {
      return result;
   }

   protected ComputationCompiler new_computation_compiler
   (
      final boolean expect_ref
   )
   {
      return
         new ComputationCompiler
         (
            macro_manager,
            anonymous_variables,
            wyrd_world,
            expect_ref
         );
   }

   protected InstructionCompiler new_instruction_compiler ()
   {
      return
         new InstructionCompiler
         (
            macro_manager,
            anonymous_variables,
            wyrd_world
         );
   }

   protected InstructionCompiler new_instruction_compiler
   (
      final List<Instruction> result
   )
   {
      return
         new InstructionCompiler
         (
            macro_manager,
            anonymous_variables,
            wyrd_world,
            result
         );
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
      /*
      final Ref element_as_ref, collection_as_ref, collection_size_as_ref;
      final Ref element_found, element_index, collection_size, next_index;
      final ComputationCompiler element_compiler, reference_compiler;
      final Type element_type;
      final List<Instruction> while_body, else_branch;

      element_compiler = new_computation_compiler(false);

      ae.get_element().get_visited_by(element_compiler);

      result.addAll(element_compiler.get_pre_instructions());

      element_type = element_compiler.get_computation().get_type();

      element_as_ref = anonymous_variable.reserve(element_type);

      result.add
      (
         new SetValue(element_as_ref, element_compiler.get_computation())
      );

      reference_compiler = new_computation_compiler(true);

      ae.get_collection().get_visited_by(reference_compiler);

      result.addAll(reference_compiler.get_pre_instructions());

      collection_as_ref = reference_compiler.get_ref();

      element_found = anonymous_variable.reserve(Type.BOOLEAN);
      element_index = anonymous_variable.reserve(Type.INT);
      collection_size = anonymous_variable.reserve(Type.INT);
      next_index = anonymous_variable.reserve(Type.INT);

      result.add
      (
         new SetValue(collection_size, new Size(collection_as_ref))
      );

      result.addAll
      (
         BinarySearch.generate
         (
            anonymous_variables,
            element_as_ref,
            collection_as_ref,
            collection_size,
            element_found,
            element_index
         )
      );

      while_body = new ArrayList<Instruction>();

      while_body.add
      (
         new SetValue
         (
            next_index,
            Operation.minus
            (
               new ValueOf(collection_size),
               new Constant(Type.INT, "1")
            )
         )
      );

      while_body.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection_as_ref,
               Collections.singletonList
               (
                  new Cast
                  (
                     new ValueOf(collection_size),
                     Type.STRING
                  )
               ),
               element_type
            ),
            new ValueOf
            (
               new RelativeRef
               (
                  collection_as_ref,
                  Collections.singletonList
                  (
                     new Cast
                     (
                        new ValueOf(next),
                        Type.STRING
                     )
                  ),
                  element_type
               )
            )
         )
      );

      while_body.add
      (
         new SetValue
         (
            collection_size,
            new ValueOf(next)
         )
      );

      else_branch = new ArrayList<Instruction>();

      else_branch.add
      (
         new While
         (
            Operation.less_than
            (
               new ValueOf(element_index),
               new ValueOf(collection_size)
            ),
            while_body
         )
      );

      else_branch.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection_as_ref,
               Collections.singletonList
               (
                  new Cast
                  (
                     new ValueOf(element_index),
                     Type.STRING
                  )
               ),
               element_type
            ),
            new ValueOf(element_as_ref)
         )
      );

      result.add
      (
         new IfElseInstruction
         (
            new ValueOf(element_found),
            new NOP(),
            else_branch
         )
      );

      anonymous_variables.release(element_as_ref);
      anonymous_variables.release(element_found);
      anonymous_variables.release(element_index);
      anonymous_variables.release(collection_size);
      anonymous_variables.release(next_index);

      reference_compiler.free_anonymous_variables();
      element_compiler.free_anonymous_variables();
      */
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

      element_compiler = new_computation_compiler(false);

      ae.get_element().get_visited_by(element_compiler);

      reference_compiler = new_computation_compiler(true);

      ae.get_collection().get_visited_by(reference_compiler);

      collection_as_ref = reference_compiler.get_ref();

      result.addAll(reference_compiler.get_pre_instructions());
      result.addAll(element_compiler.get_pre_instructions());
      result.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection_as_ref,
               Collections.singletonList
               (
                  new Cast
                  (
                     new Size(collection_as_ref),
                     Type.STRING
                  )
               ),
               element_compiler.get_computation().get_type()
            ),
            element_compiler.get_computation()
         )
      );

      reference_compiler.free_anonymous_variables();
      element_compiler.free_anonymous_variables();
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
         /* TODO: error */
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
      final ComputationCompiler cc;

      cc = new_computation_compiler(false);

      a.get_condition().get_visited_by(cc);

      result.addAll(cc.get_pre_instructions());
      result.add(new Assert(cc.get_computation()));

      cc.free_anonymous_variables();
   }

   @Override
   public void visit_clear (final tonkadur.fate.v1.lang.instruction.Clear c)
   throws Throwable
   {
      /*
       * Fate: (clear collection)
       *
       * Wyrd:
       *    <clear collection>
       */
      /*
      final ComputationCompiler reference_compiler;
      final Ref iterator, collection_ref;
      final List<Instruction> while_body;

      reference_compiler = new_computation_compiler(true);

      c.get_collection().get_visited_by(reference_compiler);

      result.addAll(reference_compiler.get_pre_instructions());

      iterator = anonymous_variables.reserve(Type.INT);

      collection_ref = reference_compiler.get_ref();

      while_body.add
      (
         new Remove
         (
            new RelativeRef
            (
               collection_ref,
               Collections.singletonList
               (
                  new Cast(new ValueOf(iterator), Type.STRING)
               )
            )
         )
      );

      while_body.add
      (
         new SetValue
         (
            iterator,
            Operation.minus(new ValueOf(iterator), new Constant(Type.INT, "1"))
         )
      );

      result.add
      (
         new While
         (
            Operation.greater_equal_than
            (
               new ValueOf(iterator),
               new Constant(Type.INT, "0")
            ),
            while_body
         )
      );

      anonymous_variables.release(iterator);
      reference_compiler.free_anonymous_variables();
      */
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
       *    (ifelse c0
       *       i0
       *       (ifelse ...
       *          ...
       *          (ifelse cn
       *             in
       *             (nop)
       *          )
       *       )
       *    )
       */
      InstructionCompiler ic;
      ComputationCompiler cc;
      List<Instruction> previous_else_branch;
      List<Instruction> current_else_branch;

      previous_else_branch = result;

      for
      (
         final
         Cons
         <
            tonkadur.fate.v1.lang.meta.Computation,
            tonkadur.fate.v1.lang.meta.Instruction
         >
         branch:
            ci.get_branches()
      )
      {
         current_else_branch = new ArrayList<Instruction>();

         ic = new_instruction_compiler();
         cc = new_computation_compiler(false);

         branch.get_car().get_visited_by(cc);

         previous_else_branch.addAll(cc.get_pre_instructions());
         previous_else_branch.add
         (
            new IfElseInstruction
            (
               cc.get_computation(),
               ic.get_result(),
               current_else_branch
            )
         );

         previous_else_branch = current_else_branch;

         cc.free_anonymous_variables();
      }

      previous_else_branch.add(new NOP());
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

      cc = new_computation_compiler(false);

      n.get_content().get_visited_by(cc);

      result.addAll(cc.get_pre_instructions());
      result.add(new Display(Collections.singletonList(cc.get_computation())));

      cc.free_anonymous_variables();
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

         cc = new_computation_compiler(false);

         fate_computation.get_visited_by(cc);

         result.addAll(cc.get_pre_instructions());

         cc_list.add(cc);
         parameters.add(cc.get_computation());
      }

      result.add(new EventCall(n.get_event().get_name(), parameters));

      for (final ComputationCompiler cc: cc_list)
      {
         cc.free_anonymous_variables();
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

      cc = new_computation_compiler(false);
      if_true_ic = new_instruction_compiler();
      if_false_ic = new_instruction_compiler();

      n.get_condition().get_visited_by(cc);
      n.get_if_true().get_visited_by(if_true_ic);
      n.get_if_false().get_visited_by(if_false_ic);

      result.addAll(cc.get_pre_instructions());
      result.add
      (
         new IfElseInstruction
         (
            cc.get_computation(),
            if_true_ic.get_result(),
            if_false_ic.get_result()
         )
      );

      cc.free_anonymous_variables();
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

      cc = new_computation_compiler(false);
      if_true_ic = new_instruction_compiler();

      n.get_condition().get_visited_by(cc);
      n.get_if_true().get_visited_by(if_true_ic);

      result.addAll(cc.get_pre_instructions());
      result.add
      (
         new IfElseInstruction
         (
            cc.get_computation(),
            if_true_ic.get_result(),
            Collections.singletonList(new NOP())
         )
      );

      cc.free_anonymous_variables();
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

         ic = new_instruction_compiler();

         fate_instruction.get_visited_by(ic);

         result.addAll(ic.get_result());
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

         cc = new_computation_compiler(true);

         fate_computation.get_visited_by(cc);

         result.addAll(cc.get_pre_instructions());

         cc_list.add(cc);
         parameters.add(cc.get_ref());
      }

      macro_manager.push(n.get_macro(), parameters);
      n.get_macro().get_root().get_visited_by(this);
      macro_manager.pop();

      for (final ComputationCompiler cc: cc_list)
      {
         cc.free_anonymous_variables();
      }
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
      final InstructionCompiler ic;

      cc = new_computation_compiler(false);
      ic = new_instruction_compiler();

      n.get_text().get_visited_by(cc);

      result.addAll(cc.get_pre_instructions());

      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_effects()
      )
      {
         fate_instruction.get_visited_by(ic);
      }

      result.add(new AddChoice(cc.get_computation(), ic.get_result()));

      cc.free_anonymous_variables();
   }

   @Override
   public void visit_player_choice_list
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoiceList n
   )
   throws Throwable
   {
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

      result.add(new ResolveChoices());
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

      elem_cc = new_computation_compiler(false);
      collection_cc = new_computation_compiler(true);

      elem = anonymous_variables.reserve(elem_cc.get_computation().get_type());
      collection_size = anonymous_variables.reserve(Type.INT);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      result.addAll(elem_cc.get_pre_instructions());
      result.addAll(collection_cc.get_pre_instructions());

      collection = collection_cc.get_ref();

      result.add(new SetValue(elem, elem_cc.get_computation()));
      result.add(new SetValue(collection_size, new Size(collection)));

      elem_cc.free_anonymous_variables();

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

         index = anonymous_variables.reserve(Type.INT);
         found = anonymous_variables.reserve(Type.BOOLEAN);

         value_of_elem = new ValueOf(elem);
         value_of_collection_size = new ValueOf(collection_size);

         result.addAll
         (
            BinarySearch.generate
            (
               anonymous_variables,
               value_of_elem,
               value_of_collection_size,
               collection,
               found,
               index
            )
         );

         result.add
         (
            new IfElseInstruction
            (
               new ValueOf(found),
               RemoveAt.generate
               (
                  anonymous_variables,
                  index,
                  value_of_collection_size,
                  collection
               ),
               Collections.singletonList(new NOP())
            )
         );

         anonymous_variables.release(index);
         anonymous_variables.release(found);
      }
      else
      {
         result.addAll
         (
            RemoveAllOf.generate
            (
               anonymous_variables,
               new ValueOf(elem),
               new ValueOf(collection_size),
               collection
            )
         );
      }

      collection_cc.free_anonymous_variables();

      anonymous_variables.release(elem);
      anonymous_variables.release(collection_size);
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

      elem_cc = new_computation_compiler(false);
      collection_cc = new_computation_compiler(true);

      elem = anonymous_variables.reserve(elem_cc.get_computation().get_type());
      collection_size = anonymous_variables.reserve(Type.INT);
      found = anonymous_variables.reserve(Type.BOOLEAN);
      index = anonymous_variables.reserve(Type.INT);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      result.addAll(elem_cc.get_pre_instructions());
      result.addAll(collection_cc.get_pre_instructions());

      collection = collection_cc.get_ref();

      value_of_collection_size = new ValueOf(collection_size);

      result.add(new SetValue(elem, elem_cc.get_computation()));
      result.add(new SetValue(collection_size, new Size(collection)));

      elem_cc.free_anonymous_variables();

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
            n.get_collection().get_type()
         ).is_set()
      )
      {
         result.addAll
         (
            BinarySearch.generate
            (
               anonymous_variables,
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
         result.addAll
         (
            IterativeSearch.generate
            (
               anonymous_variables,
               new ValueOf(elem),
               value_of_collection_size,
               collection,
               found,
               index
            )
         );
      }

      anonymous_variables.release(elem);

      result.add
      (
         new IfElseInstruction
         (
            new ValueOf(found),
            RemoveAt.generate
            (
               anonymous_variables,
               index,
               value_of_collection_size,
               collection
            ),
            Collections.singletonList(new NOP())
         )
      );

      anonymous_variables.release(index);
      anonymous_variables.release(found);
      anonymous_variables.release(collection_size);

      collection_cc.free_anonymous_variables();
   }

   @Override
   public void visit_sequence_call
   (
      final tonkadur.fate.v1.lang.instruction.SequenceCall n
   )
   throws Throwable
   {
      /*
       * Fate: (sequence_call <string>)
       * Wyrd: (sequence_call <string>)
       */
      result.add(new SequenceCall(n.get_sequence_name()));
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

      value_cc = new_computation_compiler(false);
      ref_cc = new_computation_compiler(true);

      n.get_value().get_visited_by(value_cc);
      result.addAll(value_cc.get_pre_instructions());

      n.get_reference().get_visited_by(ref_cc);
      result.addAll(ref_cc.get_pre_instructions());

      result.add
      (
         new SetValue(ref_cc.get_ref(), value_cc.get_computation())
      );

      value_cc.free_anonymous_variables();
      ref_cc.free_anonymous_variables();
   }
}
