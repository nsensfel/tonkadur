package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Break;

import tonkadur.wyrd.v1.lang.instruction.SetPC;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;


public class BreakCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Break.class;
   }

   public BreakCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      result.add
      (
         new SetPC
         (
            compiler.assembler().get_context_label_constant("breakable")
         )
      );
   }
}
