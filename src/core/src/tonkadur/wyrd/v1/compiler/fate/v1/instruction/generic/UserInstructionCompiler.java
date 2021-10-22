package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.UserInstruction;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class UserInstructionCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return UserInstruction.class;
   }

   public UserInstructionCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   /*
    * Fate: (event_call <string> c0 ... cn)
    *
    * Wyrd (event_call <string> c0 ... cn)
    */
   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
   }
}
