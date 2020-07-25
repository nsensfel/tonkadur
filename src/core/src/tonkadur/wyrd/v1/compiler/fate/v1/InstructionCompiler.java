package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.World;

public class InstructionCompiler extends FateVisitor
{
   protected final AnonymousVariableManager anonymous_variables;
   protected final World wyrd_world;
   protected final List<Instruction> result;

   public InstructionCompiler
   (
      final AnonymousVariableManager anonymous_variables,
      final World wyrd_world
   )
   {
      this.anonymous_variables = anonymous_variables;
      this.wyrd_world = wyrd_world;
      result = new ArrayList<Instruction>();
   }

   public InstructionCompiler
   (
      final AnonymousVariableManager anonymous_variables,
      final World wyrd_world,
      final List<Instruction> result
   )
   {
      this.anonymous_variables = anonymous_variables;
      this.wyrd_world = wyrd_world;
      this.result = result;
   }

   protected static void add_element_to_set
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
      element_compiler =
         new ComputationCompiler
         (
            anonymous_variables,
            wyrd_world
         );

      ae.get_element().visit(element_compiler);

      result.addAll(element_compiler.get_pre_instructions());

      element_type = element_compiler.get_computation().get_type();

      element_as_ref = anonymous_variable.reserve(element_type);

      result.add
      (
         new SetValue(element_as_ref, element_compiler.get_computation())
      );

      /**** Getting the Collection as a ref ****/
      reference_compiler =
         new ComputationCompiler
         (
            anonymous_variables,
            wyrd_world
         );

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

   protected static void add_element_to_list
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

      element_compiler =
         new ComputationCompiler
         (
            anonymous_variables,
            wyrd_world
         );

      ae.get_element().visit(element_compiler);

      reference_compiler =
         new ComputationCompiler
         (
            anonymous_variables,
            wyrd_world
         );

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
            )
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

      collection_type = ae.get_collection.get_type();

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

      cc = new ComputationCompiler(wyrd_world);

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

      reference_compiler =
         new ComputationCompiler(anonymous_variables, wyrd_world);

      c.get_collection().visit(reference_compiler);

      iterator = anonymous_variables.reserve(Type.INT);

      if (!(reference_compiler.get_computation() instanceof Ref))
      {
         /* TODO: error. */
      }

      collection_ref = (Ref) reference_compiler.get_computation();

      while_body.add
      (
         new Remove(new ValueOf(iterator), collection_ref)
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
            tonkadur.fate.v1.lang.instruction.meta.Computation,
            tonkadur.fate.v1.lang.instruction.meta.Instruction
         >
         branch:
            ci.get_branches()
      )
      {
         current_else_branch = new ArrayList<Instruction>();

         ic = new InstructionCompiler(anonymous_variables, wyrd_world);
         cc = new ComputationCompiler(anonymous_variables, wyrd_world);

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
      }

      previous_else_branch.add(Collections.singleton(new NOP());
   }

   @Override
   public void visit_display (final tonkadur.fate.v1.lang.instruction.Display n)
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_event_call
   (
      final tonkadur.fate.v1.lang.instruction.EventCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_if_else_instruction
   (
      final tonkadur.fate.v1.lang.instruction.IfElseInstruction n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_if_instruction
   (
      final tonkadur.fate.v1.lang.instruction.IfInstruction n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_instruction_list
   (
      final tonkadur.fate.v1.lang.instruction.InstructionList n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_macro_call
   (
      final tonkadur.fate.v1.lang.instruction.MacroCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_player_choice
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoice n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_player_choice_list
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoiceList n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_remove_all_of_element
   (
      final tonkadur.fate.v1.lang.instruction.RemoveAllOfElement n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_remove_element
   (
      final tonkadur.fate.v1.lang.instruction.RemoveElement n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_sequence_call
   (
      final tonkadur.fate.v1.lang.instruction.SequenceCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_set_value
   (
      final tonkadur.fate.v1.lang.instruction.SetValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }
}
