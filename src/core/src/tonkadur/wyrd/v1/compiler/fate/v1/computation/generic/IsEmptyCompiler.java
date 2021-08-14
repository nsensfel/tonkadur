package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.IsEmpty;

import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class IsEmptyCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return IsEmpty.class;
   }

   public IsEmptyCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final IsEmpty source;
      final ComputationCompiler cc;

      source = (IsEmpty) computation;

      cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(cc);

      assimilate(cc);

      result_as_computation =
         Operation.equals(new Size(cc.get_address()), Constant.ZERO);
   }
}
