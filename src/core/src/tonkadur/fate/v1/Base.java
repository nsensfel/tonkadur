package tonkadur.fate.v1;

import tonkadur.TonkadurPlugin;

import tonkadur.fate.v1.error.ErrorCategory;

import tonkadur.fate.v1.lang.computation.GenericComputation;
import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Base
{
   public static void initialize ()
   {
      ErrorCategory.initialize();

      TonkadurPlugin.register_as_loadable_superclass(GenericInstruction.class);
      TonkadurPlugin.register_as_loadable_superclass(GenericComputation.class);
   }
}
