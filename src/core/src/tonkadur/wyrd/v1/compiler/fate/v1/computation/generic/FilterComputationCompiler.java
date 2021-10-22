package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.FilterComputation;


import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class FilterComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return FilterComputation.class;
   }

   public FilterComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final FilterComputation source;
      final ComputationCompiler lambda_cc, in_collection_cc;
      final Register result;

      source = (FilterComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);
      lambda_cc.generate_address();
      assimilate(lambda_cc);

      in_collection_cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(in_collection_cc);

      if (in_collection_cc.has_init())
      {
         init_instructions.add(in_collection_cc.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, in_collection_cc.get_computation())
      );

      in_collection_cc.release_registers(init_instructions);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.FilterLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            result_as_address
         )
      );
   }
}
