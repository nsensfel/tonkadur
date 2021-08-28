package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.Map;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class MapCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Map.class;
   }

   public MapCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Map source;
      final List<Computation> params;
      final List<ComputationCompiler> param_cc_list;
      // This is one dangerous operation to do in-place, so we don't.
      final Register holder;
      final ComputationCompiler lambda_cc, collection_cc;

      source = (Map) instruction;

      params = new ArrayList<Computation>();
      param_cc_list = new ArrayList<ComputationCompiler>();

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

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(collection_cc);

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
         tonkadur.wyrd.v1.compiler.util.Clear.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_cc.get_address()
         )
      );

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.MapLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_computation(),
            holder.get_address(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
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
}
