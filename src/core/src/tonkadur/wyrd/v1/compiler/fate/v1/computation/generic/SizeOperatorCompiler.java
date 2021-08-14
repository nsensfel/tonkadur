package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.SizeOperator;

import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class SizeOperatorCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return SizeOperator.class;
   }

   public SizeOperatorCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final SizeOperator source;
      final ComputationCompiler cc;

      source = (SizeOperator) computation;

      cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(cc);

      assimilate(cc);

      result_as_computation = new Size(cc.get_address());
   }
}
