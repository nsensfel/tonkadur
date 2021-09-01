package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.SafeMerge;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class SafeMergeCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return SafeMerge.class;
   }

   public SafeMergeCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final SafeMerge source;
      final Register holder;
      final ComputationCompiler lambda_cc;
      final ComputationCompiler main_default_cc, secondary_default_cc;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      final ComputationCompiler main_collection_cc, secondary_collection_cc;

      source = (SafeMerge) instruction;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

      lambda_cc = new ComputationCompiler(compiler);
      main_default_cc = new ComputationCompiler(compiler);
      secondary_default_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      main_collection_cc = new ComputationCompiler(compiler);

      source.get_main_collection().get_visited_by(main_collection_cc);

      if (main_collection_cc.has_init())
      {
         result.add(main_collection_cc.get_init());
      }

      source.get_main_default().get_visited_by(main_default_cc);

      main_default_cc.generate_address();

      if (main_default_cc.has_init())
      {
         result.add(main_default_cc.get_init());
      }

      source.get_secondary_default().get_visited_by(secondary_default_cc);

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
         tonkadur.wyrd.v1.compiler.util.Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            main_collection_cc.get_address()
         )
      );

      secondary_collection_cc = new ComputationCompiler(compiler);

      source.get_secondary_collection().get_visited_by(secondary_collection_cc);

      if (secondary_collection_cc.has_init())
      {
         result.add(secondary_collection_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p:
            source.get_extra_parameters()
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
         tonkadur.wyrd.v1.compiler.util.MergeLambda.generate
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
               source.get_main_collection().get_type()
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
}
