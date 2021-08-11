package tonkadur.wyrd.v1;

import tonkadur.TonkadurPlugin;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class Base
{
   public static void initialize ()
   {
      TonkadurPlugin.register_as_loadable_superclass
      (
         GenericComputationCompiler.class
      );

      TonkadurPlugin.register_as_loadable_superclass
      (
         GenericInstructionCompiler.class
      );
   }
}
