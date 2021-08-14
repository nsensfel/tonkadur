package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.IndexOfOperator;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class IndexOfOperatorCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return IndexOfOperator.class;
   }

   public IndexOfOperatorCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final IndexOfOperator source;
      final ComputationCompiler elem_cc, collection_cc;
      final Register result, result_found;

      source = (IndexOfOperator) computation;

      result = reserve(Type.INT);
      result_found = reserve(Type.BOOL);

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      source.get_element().get_visited_by(elem_cc);
      source.get_collection().get_visited_by(collection_cc);

      assimilate(elem_cc);
      assimilate(collection_cc);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.IterativeSearch.generate
         (
            compiler.registers(),
            compiler.assembler(),
            elem_cc.get_computation(),
            new Size(collection_cc.get_address()),
            collection_cc.get_address(),
            result_found.get_address(),
            result_as_address
         )
      );
   }
}
