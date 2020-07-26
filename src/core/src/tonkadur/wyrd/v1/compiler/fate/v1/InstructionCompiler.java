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
import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.While;

import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.AnonymousVariableManager;

public class InstructionCompiler extends FateVisitor
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

   protected ComputationCompiler new_computation_compiler ()
   {
      return
         new ComputationCompiler
         (
            macro_manager,
            anonymous_variables,
            wyrd_world
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
       *    (set .end (size collection))
       *
       *    (while (> .index .end)
       *       (set .next (- (val .end) 1))
       *       (set collection[.index] collection[.next])
       *       (set .end (val .next))
       *    )
       * )
       */
      final Ref element_as_ref, collection_as_ref, collection_size_as_ref;
      final Ref element_found, element_index, collection_size, next_index;
      final ComputationCompiler element_compiler, reference_compiler;
      final Type element_type;
      final List<Instruction> while_body, else_branch;

      /**** Getting the Element as a ref ****/
      element_compiler = new_computation_compiler();

      ae.get_element().visit(element_compiler);

      result.addAll(element_compiler.get_pre_instructions());

      element_type = element_compiler.get_computation().get_type();

      element_as_ref = anonymous_variable.reserve(element_type);

      result.add
      (
         new SetValue(element_as_ref, element_compiler.get_computation())
      );

      /**** Getting the Collection as a ref ****/
      reference_compiler = new_computation_compiler();

      ae.get_collection().visit(reference_compiler);

      result.addAll(reference_compiler.get_pre_instructions());

      if (!(reference_compiler.get_computation() instanceof Ref))
      {
         /* TODO: error. */
      }

      collection_as_ref = (Ref) reference_compiler.get_computation();

      /**** Finding the element in the collection ****/
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

      element_compiler = new_computation_compiler();

      ae.get_element().visit(element_compiler);

      reference_compiler = new_computation_compiler();

      ae.get_collection().visit(reference_compiler);

      if (!(reference_compiler.get_computation() instanceof Ref))
      {
         /* TODO: error. */
      }

      collection_as_ref = (Ref) reference_compiler.get_computation();

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
                     new Size(reference_compiler.get_computation()),
                     Type.STRING
                  )
               )
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
      final ComputationCompiler cc;

      cc = new_computation_compiler();

      a.get_condition().visit(cc);

      result.addAll(cc.get_pre_instructions());
      result.add(new Assert(cc.get_computation()));

      cc.free_anonymous_variables();
   }

   @Override
   public void visit_clear (final tonkadur.fate.v1.lang.instruction.Clear c)
   throws Throwable
   {
      final ComputationCompiler reference_compiler;
      final Ref iterator, collection_ref;
      final List<Instruction> while_body;

      reference_compiler = new_computation_compiler();

      c.get_collection().visit(reference_compiler);

      result.addAll(reference_compiler.get_pre_instructions());

      iterator = anonymous_variables.reserve(Type.INT);

      if (!(reference_compiler.get_computation() instanceof Ref))
      {
         /* TODO: error. */
      }

      collection_ref = (Ref) reference_compiler.get_computation();

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
   }

   @Override
   public void visit_cond_instruction
   (
      final tonkadur.fate.v1.lang.instruction.CondInstruction ci
   )
   throws Throwable
   {
      InstructionCompiler ic;
      ComputationCompiler cc;
      List<Instruction> previous_else_branch;
      List<Instruction> current_else_branch;

      previous_else_branch = results;

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
         cc = new_computation_compiler();

         branch.get_car().visit(cc);

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

      previous_else_branch.add(Collections.singleton(new NOP()));
   }

   @Override
   public void visit_display (final tonkadur.fate.v1.lang.instruction.Display n)
   throws Throwable
   {
      final ComputationCompiler cc;

      cc = new_computation_compiler();

      n.get_content.visit(cc);

      result.addAll(cc.get_pre_instructions());
      result.add(new Display(cc.get_computation()));

      cc.free_anonymous_variables();
   }

   @Override
   public void visit_event_call
   (
      final tonkadur.fate.v1.lang.instruction.EventCall n
   )
   throws Throwable
   {
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

         cc = new_computation_compiler();

         fate_computation.visit(cc);

         result.addAll(cc.get_pre_instructions());

         cc_list.add(cc);
         parameters.add(cc.get_computation());
      }

      result.add
      (
         new EventCall(n.get_event().get_name(), cc.get_computation())
      );

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
      final ComputationCompiler cc;
      final InstructionCompiler if_true_ic;
      final InstructionCompiler if_false_ic;

      cc = new_computation_compiler();
      if_true_ic = new_instruction_compiler();
      if_false_ic = new_instruction_compiler();

      n.get_condition.visit(cc);
      n.get_if_true().visit(if_true_ic);
      n.get_if_false().visit(if_false_ic);

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
      final ComputationCompiler cc;
      final InstructionCompiler if_true_ic;

      cc = new_computation_compiler();
      if_true_ic = new_instruction_compiler();

      n.get_condition.visit(cc);
      n.get_if_true().visit(if_true_ic);

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
      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_content()
      )
      {
         final InstructionCompiler ic;

         ic = new_instruction_compiler();

         fate_instruction.visit(ic);

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
      final tonkadur.fate.v1.lang.meta.Instruction fate_macro_root;
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

         cc = new_computation_compiler();

         fate_computation.visit(cc);

         result.addAll(cc.get_pre_instructions());

         cc_list.add(cc);
         parameters.add(cc.get_computation());
      }

      fate_macro_root =
         macro_manager.push_context(n.get_name(), cc.get_computation());

      fate_macro_root.visit(this);

      for (final ComputationCompiler cc: cc_list)
      {
         cc.free_anonymous_variables();
      }

      macro_manager.pop_context();
   }

   @Override
   public void visit_player_choice
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoice n
   )
   throws Throwable
   {
      final ComputationCompiler cc;
      final InstructionCompiler ic;

      cc = new_computation_compiler();
      ic = new_instruction_compiler();

      n.get_text().visit(cc);

      result.addAll(cc.get_pre_instructions());

      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_effects()
      )
      {
         fate_instruction.visit(ic);
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
      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_choices()
      )
      {
         fate_instruction.visit(this);
      }

      fate_instruction.add(new ResolveChoices());
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
       * (declare_variable int .index)
       * (declare_variable int .limit)
       * (declare_variable <element_type> .elem)
       * (declare_variable int .found)
       * (set .index 0)
       * (set .limit (- (size collection) 1))
       * (set .elem element) ;; avoid re-computing element
       * (set .found 0)
       * (while (< .index .limit)
       *    (ifelse (= (var .found) 0)
       *       (
       *          (nop)
       *       )
       *       (
       *          (ifelse (= (var .elem) (var collection[.index]))
       *             (set .found (+ (var .found) 1))
       *             (nop)
       *          )
       *          (set
       *             collection[.index]
       *             (var collection[(+ (var .index) (var .found))])
       *          )
       *       )
       *    )
       *    (set .index (+ (var .index) 1))
       * )
       * (while (> (var .found) 0)
       *    (set .found (- (var .found) 1))
       *    (remove collection[(- (var .limit) (var .found))])
       * )
       */
      final ComputationCompiler elem_cc, collection_cc;
      final Ref index, limit, elem, found;
      final Ref collection_as_ref;
      final List<Instruction> while_body, while_body2, if_false_body;

      elem_cc = new_computation_compiler();
      collection_cc = new_computation_compiler();

      while_body = new ArrayList<Instruction>();
      while_body2 = new ArrayList<Instruction>();
      if_false_body = new ArrayList<Instruction>();

      /* Get element with a minimum of anonymous variables */
      n.get_element().visit(elem_cc);

      result.addAll(elem_cc.get_pre_instructions());

      elem = anonymous_variable.reserve(elem_cc.get_computation().get_type());

      result.add(new SetValue(elem, elem_cc.get_computation()));

      elem_cc.free_anonymous_variables();
      /****/

      n.get_collection().visit(collection_cc);

      if (!(collection_cc.get_computation() instanceof Ref))
      {
         /* TODO: error. */
      }

      result.addAll(collection_cc.get_pre_instructions());

      collection_as_ref = (Ref) collection_cc.get_computation();

      index = anonymous_variable.reserve(Type.INT);
      limit = anonymous_variable.reserve(Type.INT);
      found = anonymous_variable.reserve(Type.INT);

      result.addAll(collection_cc.get_pre_instructions());

      result.add(new SetValue(index, new Constant(Type.INT, "0")));
      result.add
      (
         new SetValue
         (
            limit,
            Operation.minus
            (
               new Size(collection_as_ref),
               new Constant(Type.INT, "1")
            )
         )
      );
      result.add(new SetValue(found, new Constant(Type.INT, "0")));

      if_false_body.add
      (
         new IfElseInstruction
         (
            Operation.equals
            (
               new ValueOf(elem),
               new ValueOf
               (
                  new RelativeRef
                  (
                     collection_as_ref,
                     Collections.singletonList
                     (
                        new Cast(new ValueOf(index), Type.STRING)
                     )
                  )
               )
            ),
            Collections.singletonList
            (
               new SetValue
               (
                  found,
                  Operation.plus
                  (
                     new ValueOf(found),
                     new Constant(Type.INT, "1")
                  )
               )
            ),
            new NOP()
         )
      );

      if_false_body.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection_as_ref,
               Collections.singletonList
               (
                  new Cast(new ValueOf(index), Type.STRING)
               )
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
                        Operation.plus(new ValueOf(index), new ValueOf(found)),
                        Type.STRING
                     )
                  )
               )
            )
         )
      );

      while_body.add
      (
         new IfElseInstruction
         (
            Operation.equal(new ValueOf(found), new Constant(Type.INT, "0")),
            Collections.singletonList(new NOP()),
            if_false_body
         )
      );

      while_body.add
      (
         new SetValue
         (
            index,
            Operation.plus(new ValueOf(index), new Constant(Type.INT, "1"))
         )
      );

      result.add
      (
         new While
         (
            Operation.less_than(new ValueOf(index), new ValueOf(limit)),
            while_body
         )
      );

      while_body2.add
      (
         new SetValue
         (
            found,
            Operation.minus(new ValueOf(found), new Constant(Type.INT, "1"))
         )
      );

      while_body2.add
      (
         new Remove
         (
            new RelativeRef
            (
               collection_as_ref,
               Collections.singletonList
               (
                  new Cast
                  (
                     Operation.minus(new ValueOf(limit), new ValueOf(found)),
                     Type.STRING
                  )
               )
            )
         )
      );

      result.add
      (
         new While
         (
            Operation.greater_than
            (
               new ValueOf(found),
               new Constant(Type.INT, "0")
            ),
            while_body2
         )
      );

      collection_cc.free_anonymous_variables();
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
       * (declare_variable int .index)
       * (declare_variable int .limit)
       * (declare_variable <element_type> .elem)
       * (declare_variable boolean .found)
       * (declare_variable int .next)
       * (set .index 0)
       * (set .limit (- (size collection) 1))
       * (set .elem element) ;; avoid re-computing element
       * (set .found false)
       * (while (< .index .limit)
       *    (set .next (+ (var index) 1))
       *    (ifelse (var .found)
       *       (
       *          (set collection[.index] (var collection[.next]))
       *       )
       *       (ifelse (= (var .elem) (var collection[.index]))
       *          (;; if_true_body
       *             (set .found true)
       *             (set collection[.index] (var collection[.next]))
       *          )
       *          (nop)
       *       )
       *    )
       *    (set .index (var .next))
       * )
       * (ifelse (or (var .found) (= (var .elem) (var collection[.index])))
       *    (remove collection[.limit])
       *    (nop)
       * )
       */
      final ComputationCompiler elem_cc, collection_cc;
      final Ref index, limit, elem, found, next;
      final Ref collection_as_ref;
      final List<Instruction> while_body, if_true_body;

      elem_cc = new_computation_compiler();
      collection_cc = new_computation_compiler();

      while_body = new ArrayList<Instruction>();
      if_true_body = new ArrayList<Instruction>();

      /* Minimize variable usage for element ***********/
      n.get_element().visit(elem_cc);

      result.addAll(elem_cc.get_pre_instructions());

      elem = anonymous_variable.reserve(elem_cc.get_computation().get_type());

      result.add(new SetValue(elem, elem_cc.get_computation()));

      elem_cc.free_anonymous_variables();
      /***********/

      n.get_collection().visit(collection_cc);

      result.addAll(collection_cc.get_pre_instructions());

      if (!(collection_cc.get_computation() instanceof Ref))
      {
         /* TODO: error. */
      }

      collection_as_ref = (Ref) collection_cc.get_computation();

      index = anonymous_variable.reserve(Type.INT);
      limit = anonymous_variable.reserve(Type.INT);
      found = anonymous_variable.reserve(Type.BOOLEAN);
      next = anonymous_variable.reserve(Type.INT);

      result.add(new SetValue(index, new Constant(Type.INT, "0")));
      result.add
      (
         new SetValue
         (
            limit,
            Operation.minus
            (
               new Size(collection_as_ref),
               new Constant(Type.INT, "1")
            )
         )
      );
      result.add(new SetValue(found, Constant.FALSE));

      if_true_branch.add(new SetValue(found, Constant.TRUE));
      if_true_branch.add
      (
         new SetValue
         (
            /* collection[.index] */
            new RelativeRef
            (
               collection_as_ref,
               Collections.singletonList
               (
                  new Cast(newValueOf(index), Type.STRING)
               )
            ),
            /* (var collection[.next]) */
            new ValueOf
            (
               new RelativeRef
               (
                  collection_as_ref,
                  Collections.singletonList
                  (
                     new Cast(new ValueOf(next), Type.STRING)
                  )
               )
            )
         )
      );

      while_body.add
      (
         new SetValue
         (
            next,
            Operation.plus
            (
               new ValueOf(index),
               new Constant(Type.INT, "1")
            )
         )
      );

      while_body.add
      (
         new IfElseInstruction
         (
            new ValueOf(found),
            Collections.singletonList
            (
               new SetValue
               (
                  new RelativeRef
                  (
                     collection_as_ref,
                     Collections.singletonList
                     (
                        new Cast(newValueOf(index), Type.STRING)
                     )
                  ),
                  new ValueOf
                  (
                     new RelativeRef
                     (
                        collection_as_ref,
                        Collections.singletonList
                        (
                           new Cast(new ValueOf(next), Type.STRING)
                        )
                     )
                  )
               )
            ),
            Collections.singletonList
            (
               new IfElseInstruction
               (
                  Operation.equals
                  (
                     new ValueOf(elem),
                     new ValueOf
                     (
                        new RelativeRef
                        (
                           collection_as_ref,
                           Collections.singletonList
                           (
                              new Cast(newValueOf(index), Type.STRING)
                           )
                        )
                     )
                  ),
                  if_true_branch,
                  Collections.singleton(new NOP())
               )
            )
         )
      );

      result.add
      (
         new While
         (
            Operation.less_than(new ValueOf(index), new ValueOf(limit)),
            while_body
         )
      );

      result.add
      (
         new IfElseInstruction
         (
            Operation.or
            (
               new ValueOf(found),
               Operation.equals
               (
                  new ValueOf(elem),
                  new ValueOf
                  (
                     new RelativeRef
                     (
                        collection_as_ref,
                        Collections.singletonList
                        (
                           new Cast(newValueOf(index), Type.STRING)
                        )
                     )
                  )
               )
            ),
            new Remove
            (
               new RelativeRef
               (
                  collection_as_ref,
                  Collections.singletonList
                  (
                     new Cast(newValueOf(limit), Type.STRING)
                  )
               )
            ),
            new NOP()
         )
      );

      collection_cc.free_anonymous_variables();

      anonymous_variables.release(index);
      anonymous_variables.release(limit);
      anonymous_variables.release(elem);
      anonymous_variables.release(found);
      anonymous_variables.release(next);
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

      value_cc = new_computation_compiler();
      ref_cc = new_computation_compiler();

      n.get_value().visit(value_cc);
      result.addAll(value_cc.get_pre_instructions());

      n.get_reference().visit(ref_cc);
      result.addAll(ref_cc.get_pre_instructions());

      result.add
      (
         new SetValue(ref_cc.get_computation(), value_cc.get_computation())
      );

      element_cc.free_anonymous_variables();
      ref_cc.free_anonymous_variables();
   }
}
