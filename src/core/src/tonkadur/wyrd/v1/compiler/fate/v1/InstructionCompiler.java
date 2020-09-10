package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tonkadur.error.Error;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.World;
import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.IfElseComputation;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.GetLastChoiceIndex;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.AddChoice;
import tonkadur.wyrd.v1.lang.instruction.Assert;
import tonkadur.wyrd.v1.lang.instruction.Display;
import tonkadur.wyrd.v1.lang.instruction.End;
import tonkadur.wyrd.v1.lang.instruction.EventCall;
import tonkadur.wyrd.v1.lang.instruction.Initialize;
import tonkadur.wyrd.v1.lang.instruction.PromptInteger;
import tonkadur.wyrd.v1.lang.instruction.PromptString;
import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.ResolveChoices;
import tonkadur.wyrd.v1.lang.instruction.SetPC;
import tonkadur.wyrd.v1.lang.instruction.SetValue;

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
      final ComputationCompiler element_compiler, address_compiler;
      final Address element, collection;
      final Register collection_size, element_found, element_index;
      final Type element_type;

      element_compiler = new ComputationCompiler(compiler);
      ae.get_element().get_visited_by(element_compiler);

      element_compiler.generate_address();

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      element_type = element_compiler.get_computation().get_type();
      element = element_compiler.get_address();

      address_compiler = new ComputationCompiler(compiler);
      ae.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection = address_compiler.get_address();

      element_found = compiler.registers().reserve(Type.BOOL, result);
      element_index = compiler.registers().reserve(Type.INT, result);
      collection_size = compiler.registers().reserve(Type.INT, result);

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      result.add
      (
         BinarySearch.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new ValueOf(element),
            collection_size.get_value(),
            collection,
            element_found.get_address(),
            element_index.get_address()
         )
      );

      result.add
      (
         If.generate
         (
            compiler.registers(),
            compiler.assembler(),
            Operation.not(element_found.get_value()),
            InsertAt.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element_index.get_address(),
               new ValueOf(element),
               collection_size.get_value(),
               collection
            )
         )
      );

      compiler.registers().release(element_found, result);
      compiler.registers().release(element_index, result);
      compiler.registers().release(collection_size, result);

      element_compiler.release_registers(result);
      address_compiler.release_registers(result);
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
       *    (relative_address collection ( (cast int string (size collection)) ))
       *    (element)
       * )
       */
      final Address collection_as_address;
      final ComputationCompiler element_compiler, address_compiler;

      element_compiler = new ComputationCompiler(compiler);

      ae.get_element().get_visited_by(element_compiler);

      address_compiler = new ComputationCompiler(compiler);

      ae.get_collection().get_visited_by(address_compiler);


      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      collection_as_address = address_compiler.get_address();

      result.add
      (
         new Initialize
         (
            new RelativeAddress
            (
               collection_as_address,
               new Cast
               (
                  new Size(collection_as_address),
                  Type.STRING
               ),
               element_compiler.get_computation().get_type()
            ),
            element_compiler.get_computation().get_type()
         )
      );

      result.add
      (
         new SetValue
         (
            new RelativeAddress
            (
               collection_as_address,
               new Cast
               (
                  Operation.minus
                  (
                     new Size(collection_as_address),
                     Constant.ONE
                  ),
                  Type.STRING
               ),
               element_compiler.get_computation().get_type()
            ),
            element_compiler.get_computation()
         )
      );

      address_compiler.release_registers(result);
      element_compiler.release_registers(result);
   }

   @Override
   public void visit_local_variable
   (
      final tonkadur.fate.v1.lang.instruction.LocalVariable n
   )
   throws Throwable
   {
      final Register r;

      r =
         compiler.registers().reserve
         (
            TypeCompiler.compile(compiler, n.get_variable().get_type()),
            result
         );

      compiler.registers().bind(n.get_variable().get_name(), r);
   }

   @Override
   public void visit_add_elements_of
   (
      final tonkadur.fate.v1.lang.instruction.AddElementsOf n
   )
   throws Throwable
   {
      final tonkadur.fate.v1.lang.meta.Instruction as_fate;


      as_fate =
         new tonkadur.fate.v1.lang.instruction.ForEach
         (
            n.get_origin(),
            n.get_source_collection(),
            ".secret var of doom",
            Collections.singletonList
            (
               tonkadur.fate.v1.lang.instruction.AddElement.build
               (
                  n.get_origin(),
                  new tonkadur.fate.v1.lang.computation.VariableReference
                  (
                     n.get_origin(),
                     new tonkadur.fate.v1.lang.Variable
                     (
                        n.get_origin(),
                        (
                           (tonkadur.fate.v1.lang.type.CollectionType)
                           n.get_source_collection().get_type()
                        ).get_content_type(),
                        ".secret var of doom"
                     )
                  ),
                  n.get_target_collection()
               )
            )
         );

      as_fate.get_visited_by(this);
   }

   @Override
   public void visit_add_element_at
   (
      final tonkadur.fate.v1.lang.instruction.AddElementAt n
   )
   throws Throwable
   {
      final Address collection_as_address;
      final ComputationCompiler index_compiler, element_compiler;
      final ComputationCompiler collection_compiler;
      final Register index_holder;

      index_holder = compiler.registers().reserve(Type.INT, result);

      index_compiler = new ComputationCompiler(compiler);

      n.get_index().get_visited_by(index_compiler);

      if (index_compiler.has_init())
      {
         result.add(index_compiler.get_init());
      }


      element_compiler = new ComputationCompiler(compiler);

      n.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      collection_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_compiler);

      if (collection_compiler.has_init())
      {
         result.add(collection_compiler.get_init());
      }

      result.add
      (
         new SetValue
         (
            index_holder.get_address(),
            new IfElseComputation
            (
               Operation.greater_than
               (
                  index_compiler.get_computation(),
                  new Size(collection_compiler.get_address())
               ),
               new Size(collection_compiler.get_address()),
               index_compiler.get_computation()
            )
         )
      );

      result.add
      (
         InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index_holder.get_address(),
            element_compiler.get_computation(),
            new Size(collection_compiler.get_address()),
            collection_compiler.get_address()
         )
      );

      compiler.registers().release(index_holder, result);

      index_compiler.release_registers(result);
      element_compiler.release_registers(result);
      collection_compiler.release_registers(result);
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

      cond_cc.release_registers(result);
      msg_cc.release_registers(result);
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
      final ComputationCompiler address_compiler;
      final Address collection_address;

      address_compiler = new ComputationCompiler(compiler);

      c.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection_address = address_compiler.get_address();

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new Size(collection_address),
            collection_address
         )
      );

      address_compiler.release_registers(result);
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
      final ComputationCompiler address_compiler;
      final Address collection_address;

      address_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection_address = address_compiler.get_address();

      result.add
      (
         ReverseList.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new Size(collection_address),
            collection_address
         )
      );

      address_compiler.release_registers(result);
   }

   public void visit_shuffle
   (
      final tonkadur.fate.v1.lang.instruction.Shuffle n
   )
   throws Throwable
   {
      /*
       * Fate: (shuffle collection)
       *
       * Wyrd: <shuffle collection>
       */
      final ComputationCompiler address_compiler;
      final Address collection_address;

      address_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      collection_address = address_compiler.get_address();

      /*
       * TODO
      result.add
      (
         Shuffle.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new Size(collection_address),
            collection_address
         )
      );
      */

      address_compiler.release_registers(result);
   }

   public void visit_map
   (
      final tonkadur.fate.v1.lang.instruction.Map n
   )
   throws Throwable
   {
      /* TODO */
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
      Address anon;
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

      compiler.registers().push_hierarchical_instruction_level();
      n.get_default_instruction().get_visited_by(ic);
      compiler.registers().pop_hierarchical_instruction_level(ic.result);

      previous_else_branch.add(ic.get_result());

      target_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      target_cc.generate_address();

      if (target_cc.has_init())
      {
         result.add(target_cc.get_init());
      }

      anon = target_cc.get_address();
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
         compiler.registers().push_hierarchical_instruction_level();
         branch.get_cdr().get_visited_by(ic);
         compiler.registers().pop_hierarchical_instruction_level(ic.result);

         if (cc.has_init())
         {
            current_branch.add(cc.get_init());
         }

         current_branch.add
         (
            IfElse.generate
            (
               compiler.registers(),
               compiler.assembler(),
               Operation.equals(value_of_anon, cc.get_computation()),
               ic.get_result(),
               compiler.assembler().merge(previous_else_branch)
            )
         );

         previous_else_branch = current_branch;

         cc.release_registers(result);
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

      cc.release_registers(result);
   }

   @Override
   public void visit_free (final tonkadur.fate.v1.lang.instruction.Free n)
   throws Throwable
   {
      final ComputationCompiler cc;
      final Address target;

      cc = new ComputationCompiler(compiler);

      n.get_reference().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      target = cc.get_address();

      if (target == null)
      {
         System.err.println
         (
            "[P] Argument in (free "
            + n.get_reference()
            + ") did not compile to a address."
         );
      }

      result.add(new Remove(target));

      cc.release_registers(result);
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

      compiler.registers().push_hierarchical_instruction_level();
      for (final tonkadur.fate.v1.lang.meta.Instruction i: n.get_body())
      {
         ic = new InstructionCompiler(compiler);
         i.get_visited_by(ic);

         body.add(ic.get_result());
      }
      compiler.registers().pop_hierarchical_instruction_level(body);


      if (cc.has_init())
      {
         result.add
         (
            compiler.assembler().mark_after
            (
               While.generate
               (
                  compiler.registers(),
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
                  compiler.registers(),
                  compiler.assembler(),
                  cc.get_computation(),
                  compiler.assembler().merge(body)
               ),
               end_of_loop_label
            )
         );
      }

      compiler.assembler().pop_context_label("breakable");
      cc.release_registers(result);
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

      compiler.registers().push_hierarchical_instruction_level();
      for (final tonkadur.fate.v1.lang.meta.Instruction i: n.get_body())
      {
         ic = new InstructionCompiler(compiler);
         i.get_visited_by(ic);

         pre_cond_instructions.add(ic.get_result());
      }
      compiler.registers().pop_hierarchical_instruction_level
      (
         pre_cond_instructions
      );

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
               compiler.registers(),
               compiler.assembler(),
               compiler.assembler().merge(pre_cond_instructions),
               cc.get_computation(),
               NOP.generate
               (
                  compiler.registers(),
                  compiler.assembler()
               )
            ),
            end_of_loop_label
         )
      );

      compiler.assembler().pop_context_label("breakable");
      cc.release_registers(result);
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

      compiler.registers().push_hierarchical_instruction_level();
      for (final tonkadur.fate.v1.lang.meta.Instruction i: n.get_body())
      {
         ic = new InstructionCompiler(compiler);
         i.get_visited_by(ic);

         body.add(ic.get_result());
      }
      compiler.registers().pop_hierarchical_instruction_level(body);

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
                  compiler.registers(),
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
                  compiler.registers(),
                  compiler.assembler(),
                  cc.get_computation(),
                  compiler.assembler().merge(body)
               ),
               end_of_loop_label
            )
         );
      }

      compiler.assembler().pop_context_label("breakable");
      cc.release_registers(result);
   }

   @Override
   public void visit_for_each (final tonkadur.fate.v1.lang.instruction.ForEach n)
   throws Throwable
   {
      final String end_of_loop_label;
      final ComputationCompiler cc;
      final List<Instruction> new_body;
      final Register index, collection_size, current_value;
      final Address collection;
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

      index = compiler.registers().reserve(Type.INT, result);
      collection_size = compiler.registers().reserve(Type.INT, result);

      result.add(new SetValue(index.get_address(), Constant.ZERO));

      n.get_collection().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      collection = cc.get_address();

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      member_type = ((MapType) collection.get_target_type()).get_member_type();

      current_value = compiler.registers().reserve(member_type, result);

      end_of_loop_label = compiler.assembler().generate_label("<AfterForEach>");

      compiler.assembler().push_context_label("breakable", end_of_loop_label);
      compiler.registers().bind(n.get_parameter_name(), current_value);

      new_body.add
      (
         new SetValue
         (
            current_value.get_address(),
            new ValueOf
            (
               new RelativeAddress
               (
                  collection,
                  new Cast(index.get_value(), Type.STRING),
                  member_type
               )
            )
         )
      );

      compiler.registers().push_hierarchical_instruction_level();
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
      compiler.registers().pop_hierarchical_instruction_level(new_body);

      new_body.add
      (
         new SetValue
         (
            index.get_address(),
            Operation.plus(Constant.ONE, index.get_value())
         )
      );

      result.add
      (
         compiler.assembler().mark_after
         (
            While.generate
            (
               compiler.registers(),
               compiler.assembler(),
               Operation.less_than
               (
                  index.get_value(),
                  collection_size.get_value()
               ),
               compiler.assembler.merge(new_body)
            ),
            end_of_loop_label
         )
      );
      compiler.registers().unbind(n.get_parameter_name(), result);
      compiler.assembler().pop_context_label("breakable");

      compiler.registers().release(index, result);
      compiler.registers().release(collection_size, result);

      /* Already released by the unbind above. */
      /* compiler.registers().release(current_value, result); */
   }

   @Override
   public void visit_remove_element_at
   (
      final tonkadur.fate.v1.lang.instruction.RemoveElementAt n
   )
   throws Throwable
   {
      final ComputationCompiler index_cc, collection_cc;
      final Address collection;
      final Register collection_size;

      index_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.registers().reserve(Type.INT, result);

      n.get_index().get_visited_by(index_cc);
      n.get_collection().get_visited_by(collection_cc);

      index_cc.generate_address();

      if (index_cc.has_init())
      {
         result.add(index_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection = collection_cc.get_address();

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      result.add
      (
         RemoveAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index_cc.get_address(),
            collection_size.get_value(),
            collection
         )
      );

      compiler.registers().release(collection_size, result);

      index_cc.release_registers(result);
      collection_cc.release_registers(result);
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
            compiler.registers(),
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
         compiler.registers().push_hierarchical_instruction_level();
         branch.get_cdr().get_visited_by(ic);
         compiler.registers().pop_hierarchical_instruction_level(ic.result);

         if (cc.has_init())
         {
            current_branch.add(cc.get_init());
         }

         current_branch.add
         (
            IfElse.generate
            (
               compiler.registers(),
               compiler.assembler(),
               cc.get_computation(),
               ic.get_result(),
               compiler.assembler().merge(previous_else_branch)
            )
         );

         previous_else_branch = current_branch;

         cc.release_registers(result);
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
         cc.release_registers(result);
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

      compiler.registers().push_hierarchical_instruction_level();
      n.get_if_true().get_visited_by(if_true_ic);
      compiler.registers().pop_hierarchical_instruction_level
      (
         if_true_ic.result
      );

      compiler.registers().push_hierarchical_instruction_level();
      n.get_if_false().get_visited_by(if_false_ic);
      compiler.registers().pop_hierarchical_instruction_level
      (
         if_false_ic.result
      );

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      result.add
      (
         IfElse.generate
         (
            compiler.registers(),
            compiler.assembler(),
            cc.get_computation(),
            if_true_ic.get_result(),
            if_false_ic.get_result()
         )
      );

      cc.release_registers(result);
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

      compiler.registers().push_hierarchical_instruction_level();
      n.get_if_true().get_visited_by(if_true_ic);
      compiler.registers().pop_hierarchical_instruction_level
      (
         if_true_ic.result
      );

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      result.add
      (
         If.generate
         (
            compiler.registers(),
            compiler.assembler(),
            cc.get_computation(),
            if_true_ic.get_result()
         )
      );

      cc.release_registers(result);
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
   public void visit_done
   (
      final tonkadur.fate.v1.lang.instruction.Done n
   )
   throws Throwable
   {
      result.add
      (
         compiler.assembler().merge
         (
            compiler.registers().get_finalize_context_instructions()
         )
      );
      result.add
      (
         compiler.assembler().merge
         (
            compiler.registers().get_leave_context_instructions()
         )
      );
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
      final List<Instruction> to_next, labels_only;
      final ComputationCompiler cc;
      final String start_of_effect, end_of_effect;

      to_next = new ArrayList<Instruction>();
      labels_only = new ArrayList<Instruction>();
      cc = new ComputationCompiler(compiler);

      start_of_effect = compiler.assembler().generate_label("<choice#start>");
      end_of_effect = compiler.assembler().generate_label("<choice#end>");

      n.get_text().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      labels_only.add(new AddChoice(cc.get_computation()));

      labels_only.add
      (
         new SetPC(compiler.assembler().get_label_constant(end_of_effect))
      );

      result.add
      (
         If.generate
         (
            compiler.registers(),
            compiler.assembler(),
            Operation.equals
            (
               compiler.registers().get_choice_number_holder().get_value(),
               new Constant(Type.INT, "-2")
            ),
            compiler.assembler().merge(labels_only)
         )
      );

      to_next.add
      (
         new SetValue
         (
            compiler.registers().get_choice_number_holder().get_address(),
            Operation.plus
            (
               compiler.registers().get_choice_number_holder().get_value(),
               Constant.ONE
            )
         )
      );

      to_next.add
      (
         new SetPC(compiler.assembler().get_label_constant(end_of_effect))
      );

      result.add
      (
         compiler.assembler().mark_after
         (
            If.generate
            (
               compiler.registers(),
               compiler.assembler(),
               Operation.not
               (
                  Operation.equals
                  (
                     compiler.registers().get_choice_number_holder().get_value(),
                     new GetLastChoiceIndex()
                  )
               ),
               compiler.assembler().merge(to_next)
            ),
            start_of_effect
         )
      );

      result.add
      (
         new SetValue
         (
            compiler.registers().get_rand_mode_holder().get_address(),
            new Constant(Type.INT, "0")
         )
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new Size
            (
               compiler.registers().get_rand_value_holder().get_address()
            ),
            compiler.registers().get_rand_value_holder().get_address()
         )
      );

      compiler.registers().push_hierarchical_instruction_level();
      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            n.get_effects()
      )
      {
         fate_instruction.get_visited_by(this);
      }
      compiler.registers().pop_hierarchical_instruction_level(result);

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

      cc.release_registers(result);
   }

   @Override
   public void visit_player_choice_list
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoiceList n
   )
   throws Throwable
   {
      final String start_of_choices_label, end_of_choices_label;

      start_of_choices_label =
         compiler.assembler().generate_label("<ChoicesDefinition>");

      end_of_choices_label =
         compiler.assembler().generate_label("<ChoicesResolved>");

      compiler.assembler().push_context_label("choices", end_of_choices_label);

      result.add
      (
         new SetValue
         (
            compiler.registers().get_rand_mode_holder().get_address(),
            new Constant(Type.INT, "1")
         )
      );

      result.add
      (
         compiler.assembler().mark_after
         (
            new SetValue
            (
               compiler.registers().get_choice_number_holder().get_address(),
               new Constant(Type.INT, "-2")
            ),
            start_of_choices_label
         )
      );

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

      result.add(new ResolveChoices());

      result.add
      (
         new SetValue
         (
            compiler.registers().get_choice_number_holder().get_address(),
            new Constant(Type.INT, "0")
         )
      );

      result.add
      (
         new SetValue
         (
            compiler.registers().get_rand_mode_holder().get_address(),
            new Constant(Type.INT, "-1")
         )
      );

      result.add
      (
         compiler.assembler().mark_after
         (
            new SetPC
            (
               compiler.assembler().get_label_constant(start_of_choices_label)
            ),
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
      final Register collection_size;
      final Address elem, collection;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.registers().reserve(Type.INT, result);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      elem_cc.generate_address();

      if (elem_cc.has_init())
      {
         result.add(elem_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection = collection_cc.get_address();
      elem = elem_cc.get_address();

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
            n.get_collection().get_type()
         ).is_set()
      )
      {
         final Computation value_of_elem;
         final Register index, found;

         index = compiler.registers().reserve(Type.INT, result);
         found = compiler.registers().reserve(Type.BOOL, result);

         value_of_elem = new ValueOf(elem);

         result.add
         (
            BinarySearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               new ValueOf(elem),
               collection_size.get_value(),
               collection,
               found.get_address(),
               index.get_address()
            )
         );

         elem_cc.release_registers(result);

         result.add
         (
            If.generate
            (
               compiler.registers(),
               compiler.assembler(),
               found.get_value(),
               RemoveAt.generate
               (
                  compiler.registers(),
                  compiler.assembler(),
                  index.get_address(),
                  collection_size.get_value(),
                  collection
               )
            )
         );

         compiler.registers().release(index, result);
         compiler.registers().release(found, result);
      }
      else
      {
         result.add
         (
            RemoveAllOf.generate
            (
               compiler.registers(),
               compiler.assembler(),
               new ValueOf(elem),
               collection_size.get_value(),
               collection
            )
         );

         elem_cc.release_registers(result);
      }

      collection_cc.release_registers(result);

      compiler.registers().release(collection_size, result);
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
      final Register collection_size, found, index;
      final Address elem, collection;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.registers().reserve(Type.INT, result);
      found = compiler.registers().reserve(Type.BOOL, result);
      index = compiler.registers().reserve(Type.INT, result);

      n.get_element().get_visited_by(elem_cc);
      n.get_collection().get_visited_by(collection_cc);

      elem_cc.generate_address();

      if (elem_cc.has_init())
      {
         result.add(elem_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      elem = elem_cc.get_address();
      collection = collection_cc.get_address();

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );


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
               compiler.registers(),
               compiler.assembler(),
               new ValueOf(elem),
               collection_size.get_value(),
               collection,
               found.get_address(),
               index.get_address()
            )
         );
      }
      else
      {
         result.add
         (
            IterativeSearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               new ValueOf(elem),
               collection_size.get_value(),
               collection,
               found.get_address(),
               index.get_address()
            )
         );
      }

      result.add
      (
         If.generate
         (
            compiler.registers(),
            compiler.assembler(),
            new ValueOf(found.get_address()),
            RemoveAt.generate
            (
               compiler.registers(),
               compiler.assembler(),
               index.get_address(),
               collection_size.get_value(),
               collection
            )
         )
      );

      compiler.registers().release(index, result);
      compiler.registers().release(found, result);
      compiler.registers().release(collection_size, result);

      elem_cc.release_registers(result);
      collection_cc.release_registers(result);
   }

   @Override
   public void visit_sequence_call
   (
      final tonkadur.fate.v1.lang.instruction.SequenceCall n
   )
   throws Throwable
   {
      final List<ComputationCompiler> parameter_ccs;
      final List<Computation> parameters;

      final String return_to_label;

      return_to_label =
         compiler.assembler().generate_label("<seq_call#return_to>");

      parameter_ccs = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      compiler.assembler().add_fixed_name_label(n.get_sequence_name());

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation param: n.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         param.get_visited_by(cc);

         if (cc.has_init())
         {
            result.add(cc.get_init());
         }

         parameters.add(cc.get_computation());
         parameter_ccs.add(cc);
      }

      result.addAll(compiler.registers().store_parameters(parameters));

      result.add
      (
         compiler.assembler().mark_after
         (
            compiler.assembler().merge
            (
               compiler.registers().get_visit_context_instructions
               (
                  compiler.assembler().get_label_constant
                  (
                     n.get_sequence_name()
                  ),
                  compiler.assembler().get_label_constant(return_to_label)
               )
            ),
            return_to_label
         )
      );

      for (final ComputationCompiler cc: parameter_ccs)
      {
         cc.release_registers(result);
      }
   }

   @Override
   public void visit_sequence_jump
   (
      final tonkadur.fate.v1.lang.instruction.SequenceJump n
   )
   throws Throwable
   {
      final List<ComputationCompiler> parameter_ccs;
      final List<Computation> parameters;

      parameter_ccs = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      compiler.assembler().add_fixed_name_label(n.get_sequence_name());

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation param: n.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         param.get_visited_by(cc);

         if (cc.has_init())
         {
            result.add(cc.get_init());
         }

         parameters.add(cc.get_computation());
         parameter_ccs.add(cc);
      }

      result.addAll(compiler.registers().store_parameters(parameters));

      for (final ComputationCompiler cc: parameter_ccs)
      {
         cc.release_registers(result);
      }

      /* Terminate current context */
      result.addAll
      (
         compiler.registers().get_finalize_context_instructions()
      );

      result.addAll
      (
         compiler.registers().get_jump_to_context_instructions
         (
            compiler.assembler().get_label_constant
            (
               n.get_sequence_name()
            )
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
       * Fate: (set_value address value)
       * Wyrd: (set_value address value)
       */
      final ComputationCompiler value_cc, address_cc;

      value_cc = new ComputationCompiler(compiler);
      address_cc = new ComputationCompiler(compiler);

      n.get_value().get_visited_by(value_cc);

      if (value_cc.has_init())
      {
         result.add(value_cc.get_init());
      }

      n.get_reference().get_visited_by(address_cc);

      if (address_cc.has_init())
      {
         result.add(address_cc.get_init());
      }

      result.add
      (
         new SetValue(address_cc.get_address(), value_cc.get_computation())
      );

      value_cc.release_registers(result);
      address_cc.release_registers(result);
   }

   @Override
   public void visit_prompt_integer
   (
      final tonkadur.fate.v1.lang.instruction.PromptInteger n
   )
   throws Throwable
   {
      /*
       * Fate: (prompt_integer target min max label)
       * Wyrd: (prompt_integer target min max label)
       */
      final ComputationCompiler target_cc, min_cc, max_cc, label_cc;

      target_cc = new ComputationCompiler(compiler);
      min_cc = new ComputationCompiler(compiler);
      max_cc = new ComputationCompiler(compiler);
      label_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      if (target_cc.has_init())
      {
         result.add(target_cc.get_init());
      }

      n.get_min().get_visited_by(min_cc);

      if (min_cc.has_init())
      {
         result.add(min_cc.get_init());
      }

      n.get_max().get_visited_by(max_cc);

      if (max_cc.has_init())
      {
         result.add(max_cc.get_init());
      }

      n.get_label().get_visited_by(label_cc);

      if (label_cc.has_init())
      {
         result.add(label_cc.get_init());
      }

      result.add
      (
         new PromptInteger
         (
            target_cc.get_address(),
            min_cc.get_computation(),
            max_cc.get_computation(),
            label_cc.get_computation()
         )
      );

      target_cc.release_registers(result);
      min_cc.release_registers(result);
      max_cc.release_registers(result);
      label_cc.release_registers(result);
   }

   @Override
   public void visit_prompt_string
   (
      final tonkadur.fate.v1.lang.instruction.PromptString n
   )
   throws Throwable
   {
      /*
       * Fate: (prompt_integer target min max label)
       * Wyrd: (prompt_integer target min max label)
       */
      final ComputationCompiler target_cc, min_cc, max_cc, label_cc;

      target_cc = new ComputationCompiler(compiler);
      min_cc = new ComputationCompiler(compiler);
      max_cc = new ComputationCompiler(compiler);
      label_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      if (target_cc.has_init())
      {
         result.add(target_cc.get_init());
      }

      n.get_min().get_visited_by(min_cc);

      if (min_cc.has_init())
      {
         result.add(min_cc.get_init());
      }

      n.get_max().get_visited_by(max_cc);

      if (max_cc.has_init())
      {
         result.add(max_cc.get_init());
      }

      n.get_label().get_visited_by(label_cc);

      if (label_cc.has_init())
      {
         result.add(label_cc.get_init());
      }

      result.add
      (
         new PromptString
         (
            target_cc.get_address(),
            min_cc.get_computation(),
            max_cc.get_computation(),
            label_cc.get_computation()
         )
      );

      target_cc.release_registers(result);
      min_cc.release_registers(result);
      max_cc.release_registers(result);
      label_cc.release_registers(result);
   }
}
