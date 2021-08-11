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
import tonkadur.wyrd.v1.lang.computation.New;

import tonkadur.wyrd.v1.lang.instruction.AddTextOption;
import tonkadur.wyrd.v1.lang.instruction.AddEventOption;
import tonkadur.wyrd.v1.lang.instruction.Assert;
import tonkadur.wyrd.v1.lang.instruction.Display;
import tonkadur.wyrd.v1.lang.instruction.End;
import tonkadur.wyrd.v1.lang.instruction.ExtraInstruction;
import tonkadur.wyrd.v1.lang.instruction.Initialize;
import tonkadur.wyrd.v1.lang.instruction.PromptInteger;
import tonkadur.wyrd.v1.lang.instruction.PromptString;
import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.ResolveChoice;
import tonkadur.wyrd.v1.lang.instruction.SetPC;
import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.AddElement;
import tonkadur.wyrd.v1.compiler.util.AddElementsOf;
import tonkadur.wyrd.v1.compiler.util.BinarySearch;
import tonkadur.wyrd.v1.compiler.util.Clear;
import tonkadur.wyrd.v1.compiler.util.FilterLambda;
import tonkadur.wyrd.v1.compiler.util.If;
import tonkadur.wyrd.v1.compiler.util.IfElse;
import tonkadur.wyrd.v1.compiler.util.IndexedFilterLambda;
import tonkadur.wyrd.v1.compiler.util.IndexedMapLambda;
import tonkadur.wyrd.v1.compiler.util.IndexedMergeLambda;
import tonkadur.wyrd.v1.compiler.util.IndexedPartitionLambda;
import tonkadur.wyrd.v1.compiler.util.InsertAt;
import tonkadur.wyrd.v1.compiler.util.InstructionManager;
import tonkadur.wyrd.v1.compiler.util.IterativeSearch;
import tonkadur.wyrd.v1.compiler.util.MapLambda;
import tonkadur.wyrd.v1.compiler.util.MergeLambda;
import tonkadur.wyrd.v1.compiler.util.NOP;
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

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

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
/*
   @Override
   public void visit_remove_elements_of
   (
      final tonkadur.fate.v1.lang.instruction.RemoveElementsOf n
   )
   throws Throwable
   {
      final ComputationCompiler collection_in_cc, collection_cc;

      collection_cc = new ComputationCompiler(compiler);

      n.get_target_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection_in_cc = new ComputationCompiler(compiler);

      n.get_source_collection().get_visited_by(collection_in_cc);

      if (collection_in_cc.has_init())
      {
         result.add(collection_in_cc.get_init());
      }

      result.add
      (
         RemoveElementsOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_in_cc.get_address(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_target_collection().get_type()
            ).is_set()
         )
      );

      collection_cc.release_registers(result);
      collection_in_cc.release_registers(result);
   }
*/
/*
   @Override
   public void visit_add_elements_of
   (
      final tonkadur.fate.v1.lang.instruction.AddElementsOf n
   )
   throws Throwable
   {
      final ComputationCompiler collection_in_cc, collection_cc;

      collection_cc = new ComputationCompiler(compiler);

      n.get_target_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection_in_cc = new ComputationCompiler(compiler);

      n.get_source_collection().get_visited_by(collection_in_cc);

      if (collection_in_cc.has_init())
      {
         result.add(collection_in_cc.get_init());
      }

      result.add
      (
         AddElementsOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_in_cc.get_address(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_target_collection().get_type()
            ).is_set()
         )
      );

      collection_cc.release_registers(result);
      collection_in_cc.release_registers(result);
   }
*/
/*
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
               new IfElseComputation
               (
                  Operation.less_than
                  (
                     index_compiler.get_computation(),
                     Constant.ZERO
                  ),
                  Constant.ZERO,
                  index_compiler.get_computation()
               )
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
*/
/*
   @Override
   public void visit_add_element
   (
      final tonkadur.fate.v1.lang.instruction.AddElement n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, element_compiler;

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      n.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      result.add
      (
         AddElement.generate
         (
            compiler.registers(),
            compiler.assembler(),
            element_compiler.get_computation(),
            address_compiler.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
            ).is_set()
         )
      );

      element_compiler.release_registers(result);
      address_compiler.release_registers(result);
   }
*/

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

/*
   @Override
   public void visit_clear (final tonkadur.fate.v1.lang.instruction.Clear c)
   throws Throwable
   {
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
            collection_address
         )
      );

      address_compiler.release_registers(result);
   }
*/
/*
   public void visit_reverse_list
   (
      final tonkadur.fate.v1.lang.instruction.ReverseList n
   )
   throws Throwable
   {
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
*/
/*
   @Override
   public void visit_shuffle
   (
      final tonkadur.fate.v1.lang.instruction.Shuffle n
   )
   throws Throwable
   {
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
         Shuffle.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_address
         )
      );

      address_compiler.release_registers(result);
   }
*/
   @Override
   public void visit_set_fields
   (
      final tonkadur.fate.v1.lang.instruction.SetFields n
   )
   throws Throwable
   {
      final ComputationCompiler target_cc;
      final Address target;

      target_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(target_cc);

      target = target_cc.get_address();

      if (target_cc.has_init())
      {
         result.add(target_cc.get_init());
      }
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
            result.add(cc.get_init());
         }

         result.add
         (
            new SetValue
            (
               new RelativeAddress
               (
                  target,
                  new Constant(Type.STRING, entry.get_car()),
                  cc.get_computation().get_type()
               ),
               cc.get_computation()
            )
         );

         cc.release_registers(result);
      }

      target_cc.release_registers(result);
   }

/*
   @Override
   public void visit_map
   (
      final tonkadur.fate.v1.lang.instruction.Map n
   )
   throws Throwable
   {
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      // This is one dangerous operation to do in-place, so we don't.
      final Register holder;
      final ComputationCompiler lambda_cc, collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      holder =
         compiler.registers().reserve
         (
            collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         new SetValue(holder.get_address(), collection_cc.get_computation())
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_cc.get_address()
         )
      );

      result.add
      (
         MapLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            holder.get_address(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
            ).is_set(),
            params
         )
      );

      lambda_cc.release_registers(result);
      collection_cc.release_registers(result);
      compiler.registers().release(holder, result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_sort
   (
      final tonkadur.fate.v1.lang.instruction.Sort n
   )
   throws Throwable
   {
      final ComputationCompiler lambda_cc;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler collection_cc;
      final Register sorted_result;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      sorted_result =
         compiler.registers().reserve
         (
            collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         Sort.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            collection_cc.get_address(),
            sorted_result.get_address(),
            params
         )
      );

      result.add
      (
         new SetValue(collection_cc.get_address(), sorted_result.get_value())
      );

      compiler.registers().release(sorted_result, result);

      collection_cc.release_registers(result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   private void visit_merge_with_defaults
   (
      final tonkadur.fate.v1.lang.instruction.Merge n
   )
   throws Throwable
   {
      final Register holder;
      final ComputationCompiler lambda_cc;
      final ComputationCompiler main_default_cc, secondary_default_cc;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler main_collection_cc, secondary_collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      lambda_cc = new ComputationCompiler(compiler);
      main_default_cc = new ComputationCompiler(compiler);
      secondary_default_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      main_collection_cc = new ComputationCompiler(compiler);

      n.get_main_collection().get_visited_by(main_collection_cc);

      if (main_collection_cc.has_init())
      {
         result.add(main_collection_cc.get_init());
      }

      n.get_main_default().get_visited_by(main_default_cc);

      main_default_cc.generate_address();

      if (main_default_cc.has_init())
      {
         result.add(main_default_cc.get_init());
      }

      n.get_secondary_default().get_visited_by(secondary_default_cc);

      secondary_default_cc.generate_address();

      if (secondary_default_cc.has_init())
      {
         result.add(secondary_default_cc.get_init());
      }

      holder =
         compiler.registers().reserve
         (
            main_collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         new SetValue
         (
            holder.get_address(),
            main_collection_cc.get_computation()
         )
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            main_collection_cc.get_address()
         )
      );

      secondary_collection_cc = new ComputationCompiler(compiler);

      n.get_secondary_collection().get_visited_by(secondary_collection_cc);

      if (secondary_collection_cc.has_init())
      {
         result.add(secondary_collection_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      result.add
      (
         MergeLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            secondary_default_cc.get_computation(),
            secondary_collection_cc.get_address(),
            main_default_cc.get_computation(),
            holder.get_address(),
            main_collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_main_collection().get_type()
            ).is_set(),
            params
         )
      );

      main_collection_cc.release_registers(result);
      secondary_collection_cc.release_registers(result);
      main_default_cc.release_registers(result);
      secondary_default_cc.release_registers(result);
      compiler.registers().release(holder, result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_merge
   (
      final tonkadur.fate.v1.lang.instruction.Merge n
   )
   throws Throwable
   {
      // This is one dangerous operation to do in-place, so we don't.

      if (n.get_main_default() != null)
      {
         visit_merge_with_defaults(n);
         return;
      }

      final Register holder;
      final ComputationCompiler lambda_cc;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler main_collection_cc, secondary_collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      main_collection_cc = new ComputationCompiler(compiler);

      n.get_main_collection().get_visited_by(main_collection_cc);

      if (main_collection_cc.has_init())
      {
         result.add(main_collection_cc.get_init());
      }

      holder =
         compiler.registers().reserve
         (
            main_collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         new SetValue
         (
            holder.get_address(),
            main_collection_cc.get_computation()
         )
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            main_collection_cc.get_address()
         )
      );

      secondary_collection_cc = new ComputationCompiler(compiler);

      n.get_secondary_collection().get_visited_by(secondary_collection_cc);

      if (secondary_collection_cc.has_init())
      {
         result.add(secondary_collection_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      result.add
      (
         MergeLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            secondary_collection_cc.get_address(),
            holder.get_address(),
            main_collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_main_collection().get_type()
            ).is_set(),
            params
         )
      );

      main_collection_cc.release_registers(result);
      secondary_collection_cc.release_registers(result);
      compiler.registers().release(holder, result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   private void visit_indexed_merge_with_defaults
   (
      final tonkadur.fate.v1.lang.instruction.IndexedMerge n
   )
   throws Throwable
   {
      final Register holder;
      final ComputationCompiler lambda_cc;
      final ComputationCompiler main_default_cc, secondary_default_cc;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler main_collection_cc, secondary_collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      lambda_cc = new ComputationCompiler(compiler);
      main_default_cc = new ComputationCompiler(compiler);
      secondary_default_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      main_collection_cc = new ComputationCompiler(compiler);

      n.get_main_collection().get_visited_by(main_collection_cc);

      if (main_collection_cc.has_init())
      {
         result.add(main_collection_cc.get_init());
      }

      n.get_main_default().get_visited_by(main_default_cc);

      main_default_cc.generate_address();

      if (main_default_cc.has_init())
      {
         result.add(main_default_cc.get_init());
      }

      n.get_secondary_default().get_visited_by(secondary_default_cc);

      secondary_default_cc.generate_address();

      if (secondary_default_cc.has_init())
      {
         result.add(secondary_default_cc.get_init());
      }

      holder =
         compiler.registers().reserve
         (
            main_collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         new SetValue
         (
            holder.get_address(),
            main_collection_cc.get_computation()
         )
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            main_collection_cc.get_address()
         )
      );

      secondary_collection_cc = new ComputationCompiler(compiler);

      n.get_secondary_collection().get_visited_by(secondary_collection_cc);

      if (secondary_collection_cc.has_init())
      {
         result.add(secondary_collection_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      result.add
      (
         IndexedMergeLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            secondary_default_cc.get_computation(),
            secondary_collection_cc.get_address(),
            main_default_cc.get_computation(),
            holder.get_address(),
            main_collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_main_collection().get_type()
            ).is_set(),
            params
         )
      );

      main_collection_cc.release_registers(result);
      secondary_collection_cc.release_registers(result);
      main_default_cc.release_registers(result);
      secondary_default_cc.release_registers(result);
      compiler.registers().release(holder, result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_indexed_merge
   (
      final tonkadur.fate.v1.lang.instruction.IndexedMerge n
   )
   throws Throwable
   {
      if (n.get_main_default() != null)
      {
         visit_indexed_merge_with_defaults(n);
         return;
      }

      // This is one dangerous operation to do in-place, so we don't.
      final Register holder;
      final ComputationCompiler lambda_cc;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler main_collection_cc, secondary_collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      main_collection_cc = new ComputationCompiler(compiler);

      n.get_main_collection().get_visited_by(main_collection_cc);

      if (main_collection_cc.has_init())
      {
         result.add(main_collection_cc.get_init());
      }

      holder =
         compiler.registers().reserve
         (
            main_collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         new SetValue
         (
            holder.get_address(),
            main_collection_cc.get_computation()
         )
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            main_collection_cc.get_address()
         )
      );

      secondary_collection_cc = new ComputationCompiler(compiler);

      n.get_secondary_collection().get_visited_by(secondary_collection_cc);

      if (secondary_collection_cc.has_init())
      {
         result.add(secondary_collection_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      result.add
      (
         IndexedMergeLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            secondary_collection_cc.get_address(),
            holder.get_address(),
            main_collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_main_collection().get_type()
            ).is_set(),
            params
         )
      );

      main_collection_cc.release_registers(result);
      secondary_collection_cc.release_registers(result);
      compiler.registers().release(holder, result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_partition
   (
      final tonkadur.fate.v1.lang.instruction.Partition n
   )
   throws Throwable
   {
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler lambda_cc, collection_in_cc, collection_out_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_in_cc = new ComputationCompiler(compiler);

      n.get_collection_in().get_visited_by(collection_in_cc);

      if (collection_in_cc.has_init())
      {
         result.add(collection_in_cc.get_init());
      }

      collection_out_cc = new ComputationCompiler(compiler);

      n.get_collection_out().get_visited_by(collection_out_cc);

      if (collection_out_cc.has_init())
      {
         result.add(collection_out_cc.get_init());
      }

      result.add
      (
         PartitionLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            collection_in_cc.get_address(),
            collection_out_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection_out().get_type()
            ).is_set(),
            params
         )
      );

      lambda_cc.release_registers(result);
      collection_in_cc.release_registers(result);
      collection_out_cc.release_registers(result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_indexed_partition
   (
      final tonkadur.fate.v1.lang.instruction.IndexedPartition n
   )
   throws Throwable
   {
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler lambda_cc, collection_in_cc, collection_out_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_in_cc = new ComputationCompiler(compiler);

      n.get_collection_in().get_visited_by(collection_in_cc);

      if (collection_in_cc.has_init())
      {
         result.add(collection_in_cc.get_init());
      }

      collection_out_cc = new ComputationCompiler(compiler);

      n.get_collection_out().get_visited_by(collection_out_cc);

      if (collection_out_cc.has_init())
      {
         result.add(collection_out_cc.get_init());
      }

      result.add
      (
         IndexedPartitionLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            collection_in_cc.get_address(),
            collection_out_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection_out().get_type()
            ).is_set(),
            params
         )
      );

      lambda_cc.release_registers(result);
      collection_in_cc.release_registers(result);
      collection_out_cc.release_registers(result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_sublist
   (
      final tonkadur.fate.v1.lang.instruction.SubList n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, start_compiler, end_compiler;
      final Register result_holder;

      address_compiler = new ComputationCompiler(compiler);
      start_compiler = new ComputationCompiler(compiler);
      end_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      n.get_start_index().get_visited_by(start_compiler);

      if (start_compiler.has_init())
      {
         result.add(start_compiler.get_init());
      }

      n.get_end_index().get_visited_by(end_compiler);

      if (end_compiler.has_init())
      {
         result.add(end_compiler.get_init());
      }

      result_holder =
         compiler.registers().reserve
         (
            address_compiler.get_computation().get_type(),
            result
         );

      result.add
      (
         SubList.generate
         (
            compiler.registers(),
            compiler.assembler(),
            start_compiler.get_computation(),
            end_compiler.get_computation(),
            address_compiler.get_address(),
            result_holder.get_address()
         )
      );

      result.add
      (
         new SetValue
         (
            address_compiler.get_address(),
            result_holder.get_value()
         )
      );

      compiler.registers().release(result_holder, result);

      address_compiler.release_registers(result);
      start_compiler.release_registers(result);
      end_compiler.release_registers(result);
   }
*/
/*
   @Override
   public void visit_push_element
   (
      final tonkadur.fate.v1.lang.instruction.PushElement n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, element_compiler;
      final Register collection_size, index;

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      n.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      collection_size = compiler.registers().reserve(Type.INT, result);
      index = compiler.registers().reserve(Type.INT, result);

      result.add
      (
         new SetValue
         (
            collection_size.get_address(),
            new Size(address_compiler.get_address())
         )
      );

      result.add
      (
         new SetValue
         (
            index.get_address(),
            (n.is_from_left() ? Constant.ZERO : collection_size.get_value())
         )
      );

      result.add
      (
         InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index.get_address(),
            element_compiler.get_computation(),
            collection_size.get_value(),
            address_compiler.get_address()
         )
      );

      address_compiler.release_registers(result);
      element_compiler.release_registers(result);

      compiler.registers().release(collection_size, result);
      compiler.registers().release(index, result);
   }
*/
/*
   @Override
   public void visit_pop_element
   (
      final tonkadur.fate.v1.lang.instruction.PopElement n
   )
   throws Throwable
   {
      final ComputationCompiler address_compiler, element_compiler;

      address_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         result.add(address_compiler.get_init());
      }

      n.get_storage_pointer().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      result.add
      (
         PopElement.generate
         (
            compiler.registers(),
            compiler.assembler(),
            address_compiler.get_address(),
            element_compiler.get_computation(),
            n.is_from_left()
         )
      );

      address_compiler.release_registers(result);
      element_compiler.release_registers(result);
   }
*/
/*
   @Override
   public void visit_filter
   (
      final tonkadur.fate.v1.lang.instruction.Filter n
   )
   throws Throwable
   {
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler lambda_cc, collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      result.add
      (
         FilterLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            collection_cc.get_address(),
            params
         )
      );

      lambda_cc.release_registers(result);
      collection_cc.release_registers(result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_indexed_filter
   (
      final tonkadur.fate.v1.lang.instruction.IndexedFilter n
   )
   throws Throwable
   {
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler lambda_cc, collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      result.add
      (
         IndexedFilterLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            collection_cc.get_address(),
            params
         )
      );

      lambda_cc.release_registers(result);
      collection_cc.release_registers(result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
/*
   @Override
   public void visit_indexed_map
   (
      final tonkadur.fate.v1.lang.instruction.IndexedMap n
   )
   throws Throwable
   {
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      // This is one dangerous operation to do in-place, so we don't.
      final Register holder;
      final ComputationCompiler lambda_cc, collection_cc;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            n.get_extra_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         p.get_visited_by(param_cc);

         // Let's not re-compute the parameters on every iteration.
         param_cc.generate_address();

         if (param_cc.has_init())
         {
            result.add(param_cc.get_init());
         }

         param_cc_list.add(param_cc);

         params.add(param_cc.get_computation());
      }

      lambda_cc = new ComputationCompiler(compiler);

      n.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      n.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      holder =
         compiler.registers().reserve
         (
            collection_cc.get_computation().get_type(),
            result
         );

      result.add
      (
         new SetValue(holder.get_address(), collection_cc.get_computation())
      );

      result.add
      (
         Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_cc.get_address()
         )
      );

      result.add
      (
         IndexedMapLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            holder.get_address(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               n.get_collection().get_type()
            ).is_set(),
            params
         )
      );

      lambda_cc.release_registers(result);
      collection_cc.release_registers(result);
      compiler.registers().release(holder, result);

      for (final ComputationCompiler cc: param_cc_list)
      {
         cc.release_registers(result);
      }
   }
*/
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
/*
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
*/
/*
   @Override
   public void visit_allocate
   (
      final tonkadur.fate.v1.lang.instruction.Allocate n
   )
   throws Throwable
   {
      final ComputationCompiler cc;
      final Address target;

      cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      target = cc.get_address();

      if (target == null)
      {
         System.err.println
         (
            "[P] Argument in (allocate "
            + n.get_target()
            + ") did not compile to a address."
         );
      }

      result.add
      (
         new SetValue
         (
            target,
            new New(TypeCompiler.compile(compiler, n.get_allocated_type()))
         )
      );

      cc.release_registers(result);
   }
*/
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

      compiler.registers().push_hierarchical_instruction_level();
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
      compiler.registers().pop_hierarchical_instruction_level(result);
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
/*
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
*/
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
   public void visit_extra_instruction
   (
      final tonkadur.fate.v1.lang.instruction.ExtraInstructionInstance n
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

      result.add
      (
         new ExtraInstruction
         (
            n.get_instruction_type().get_name(),
            parameters
         )
      );

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
      final List<Instruction> instructions;
      final ComputationCompiler cc;

      instructions = new ArrayList<Instruction>();
      cc = new ComputationCompiler(compiler);

      n.get_condition().get_visited_by(cc);

      compiler.registers().push_hierarchical_instruction_level();

      for (final tonkadur.fate.v1.lang.meta.Instruction instr: n.get_if_true())
      {
         final InstructionCompiler if_true_ic;

         if_true_ic = new InstructionCompiler(compiler);

         instr.get_visited_by(if_true_ic);

         instructions.add(if_true_ic.get_result());
      }

      compiler.registers().pop_hierarchical_instruction_level
      (
         instructions
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
            compiler.assembler().merge(instructions)
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
   public void visit_text_option
   (
      final tonkadur.fate.v1.lang.instruction.TextOption n
   )
   throws Throwable
   {
      /*
       * Fate: (text_option label i0)
       *
       * Wyrd (add_text_option label i0)
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

      labels_only.add(new AddTextOption(cc.get_computation()));

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
   public void visit_event_option
   (
      final tonkadur.fate.v1.lang.instruction.EventOption n
   )
   throws Throwable
   {
      /*
       * Fate: (event_option label i0)
       *
       * Wyrd (add_event_option label i0)
       */
      final List<Instruction> to_next, labels_only;
      final List<ComputationCompiler> params_cc;
      final List<Computation> params;
      final String start_of_effect, end_of_effect;

      to_next = new ArrayList<Instruction>();
      labels_only = new ArrayList<Instruction>();
      params = new ArrayList<Computation>();
      params_cc = new ArrayList<ComputationCompiler>();

      start_of_effect = compiler.assembler().generate_label("<choice#start>");
      end_of_effect = compiler.assembler().generate_label("<choice#end>");

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

         params.add(cc.get_computation());
      }

      labels_only.add
      (
         new AddEventOption(n.get_input_event().get_name(), params)
      );

      for (final ComputationCompiler cc: params_cc)
      {
         cc.release_registers(labels_only);
      }

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
   }


   @Override
   public void visit_player_choice
   (
      final tonkadur.fate.v1.lang.instruction.PlayerChoice n
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
            n.get_entries()
      )
      {
         fate_instruction.get_visited_by(this);
      }

      compiler.assembler().pop_context_label("choices");

      result.add(new ResolveChoice());

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

//   @Override
//   public void visit_remove_all_of_element
//   (
//      final tonkadur.fate.v1.lang.instruction.RemoveAllOfElement n
//   )
//   throws Throwable
//   {
//      /*
//       * Fate:
//       * (remove_all_of element collection)
//       *
//       * Wyrd:
//       * (declare_variable <element_type> .elem)
//       * (declare_variable int .collection_size)
//       *
//       * (set .elem element)
//       * (set .collection_size (size collection))
//       *
//       * <if collection is a list:
//       *    <remove_all (var .elem) (var .collection_size) collection>
//       * >
//       * <if collection is a set:
//       *    (declare_variable bool .found)
//       *    (declare_variable int .index)
//       *
//       *    <binary_search
//       *       (var .elem)
//       *       (var .collection_size)
//       *       collection
//       *       .found
//       *       .index
//       *    >
//       *    (ifelse (var .found)
//       *       <remove_at (var .index) (var .collection_size) collection>
//       *       (nop)
//       *    )
//       * >
//       */
//      final ComputationCompiler elem_cc, collection_cc;
//      final Register collection_size;
//      final Address elem, collection;
//
//      elem_cc = new ComputationCompiler(compiler);
//      collection_cc = new ComputationCompiler(compiler);
//
//      collection_size = compiler.registers().reserve(Type.INT, result);
//
//      n.get_element().get_visited_by(elem_cc);
//      n.get_collection().get_visited_by(collection_cc);
//
//      elem_cc.generate_address();
//
//      if (elem_cc.has_init())
//      {
//         result.add(elem_cc.get_init());
//      }
//
//      if (collection_cc.has_init())
//      {
//         result.add(collection_cc.get_init());
//      }
//
//      collection = collection_cc.get_address();
//      elem = elem_cc.get_address();
//
//      result.add
//      (
//         new SetValue(collection_size.get_address(), new Size(collection))
//      );
//
//      if
//      (
//         (
//            (tonkadur.fate.v1.lang.type.CollectionType)
//            n.get_collection().get_type()
//         ).is_set()
//      )
//      {
//         final Computation value_of_elem;
//         final Register index, found;
//
//         index = compiler.registers().reserve(Type.INT, result);
//         found = compiler.registers().reserve(Type.BOOL, result);
//
//         value_of_elem = new ValueOf(elem);
//
//         result.add
//         (
//            BinarySearch.generate
//            (
//               compiler.registers(),
//               compiler.assembler(),
//               new ValueOf(elem),
//               collection_size.get_value(),
//               collection,
//               found.get_address(),
//               index.get_address()
//            )
//         );
//
//         elem_cc.release_registers(result);
//
//         result.add
//         (
//            If.generate
//            (
//               compiler.registers(),
//               compiler.assembler(),
//               found.get_value(),
//               RemoveAt.generate
//               (
//                  compiler.registers(),
//                  compiler.assembler(),
//                  index.get_address(),
//                  collection_size.get_value(),
//                  collection
//               )
//            )
//         );
//
//         compiler.registers().release(index, result);
//         compiler.registers().release(found, result);
//      }
//      else
//      {
//         result.add
//         (
//            RemoveAllOf.generate
//            (
//               compiler.registers(),
//               compiler.assembler(),
//               new ValueOf(elem),
//               collection_size.get_value(),
//               collection
//            )
//         );
//
//         elem_cc.release_registers(result);
//      }
//
//      collection_cc.release_registers(result);
//
//      compiler.registers().release(collection_size, result);
//   }

//   @Override
//   public void visit_remove_element
//   (
//      final tonkadur.fate.v1.lang.instruction.RemoveElement n
//   )
//   throws Throwable
//   {
//      /*
//       * Fate:
//       * (remove_element element collection)
//       *
//       * Wyrd:
//       * (declare_variable <element_type> .elem)
//       * (declare_variable int .collection_size)
//       * (declare_variable boolean .found)
//       * (declare_variable int .index)
//       *
//       * (set .elem element)
//       * (set .collection_size (size collection))
//       *
//       * <if collection is a set:
//       *    <BinarySearch
//       *       (var .elem)
//       *       (var .collection_size)
//       *       collection
//       *       .found
//       *       .index
//       *    >
//       * >
//       * <if collection is a list:
//       *    <IterativeSearch
//       *       (var .elem)
//       *       (var .collection_size)
//       *       collection
//       *       .found
//       *       .index
//       *    >
//       * >
//       *
//       * (if (var .found)
//       *    <remove_at (var index) (var .collection_size) collection>
//       *    (nop)
//       * )
//       */
//      final ComputationCompiler elem_cc, collection_cc;
//
//      elem_cc = new ComputationCompiler(compiler);
//      collection_cc = new ComputationCompiler(compiler);
//
//      n.get_element().get_visited_by(elem_cc);
//      n.get_collection().get_visited_by(collection_cc);
//
//      elem_cc.generate_address();
//
//      if (elem_cc.has_init())
//      {
//         result.add(elem_cc.get_init());
//      }
//
//      if (collection_cc.has_init())
//      {
//         result.add(collection_cc.get_init());
//      }
//
//      result.add
//      (
//         RemoveOneOf.generate
//         (
//            compiler.registers(),
//            compiler.assembler(),
//            elem_cc.get_computation(),
//            collection_cc.get_address(),
//            (
//               (tonkadur.fate.v1.lang.type.CollectionType)
//               n.get_collection().get_type()
//            ).is_set()
//         )
//      );
//
//      elem_cc.release_registers(result);
//      collection_cc.release_registers(result);
//   }
//
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
   public void visit_sequence_variable_call
   (
      final tonkadur.fate.v1.lang.instruction.SequenceVariableCall n
   )
   throws Throwable
   {
      final ComputationCompiler sequence_cc;
      final List<ComputationCompiler> parameter_ccs;
      final List<Computation> parameters;

      final String return_to_label;

      return_to_label =
         compiler.assembler().generate_label("<seq_call#return_to>");

      sequence_cc = new ComputationCompiler(compiler);
      parameter_ccs = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      n.get_sequence().get_visited_by(sequence_cc);

      if (sequence_cc.has_init())
      {
         result.add(sequence_cc.get_init());
      }

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
                  sequence_cc.get_computation(),
                  compiler.assembler().get_label_constant(return_to_label)
               )
            ),
            return_to_label
         )
      );

      sequence_cc.release_registers(result);

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
   public void visit_sequence_variable_jump
   (
      final tonkadur.fate.v1.lang.instruction.SequenceVariableJump n
   )
   throws Throwable
   {
      final ComputationCompiler sequence_cc;
      final List<ComputationCompiler> parameter_ccs;
      final List<Computation> parameters;

      sequence_cc = new ComputationCompiler(compiler);
      parameter_ccs = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      n.get_sequence().get_visited_by(sequence_cc);

      if (sequence_cc.has_init())
      {
         result.add(sequence_cc.get_init());
      }

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
            sequence_cc.get_computation()
         )
      );

      sequence_cc.release_registers(result);
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
            target_cc.get_computation(),
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
            target_cc.get_computation(),
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
   public void visit_generic_instruction
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction n
   )
   throws Throwable
   {
      final InstructionCompiler ic;

      ic = GenericInstructionCompiler.handle(compiler, n);

      this.result.addAll(ic.result);
   }
}
