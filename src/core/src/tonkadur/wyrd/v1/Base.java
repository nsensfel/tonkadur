package tonkadur.wyrd.v1;

import tonkadur.TonkadurPlugin;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class Base
{
   public static void initialize ()
   {
      TonkadurPlugin.register_as_loadable_superclass
      (
         GenericComputationCompiler.class
      );
   }
}
