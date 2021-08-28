package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.wyrd.v1.lang.instruction.End;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;


public class EndCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return tonkadur.fate.v1.lang.instruction.generic.End.class;
   }

   public EndCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      result.add(new End());
   }
}
