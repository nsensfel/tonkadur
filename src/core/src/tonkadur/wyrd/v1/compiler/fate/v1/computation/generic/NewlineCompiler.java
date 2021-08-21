package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.Newline;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class NewlineCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return Newline.class;
   }

   public NewlineCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation _computation
   )
   throws Throwable
   {
      result_as_computation = new tonkadur.wyrd.v1.lang.computation.Newline();
   }
}
