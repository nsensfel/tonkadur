package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Done;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class DoneCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Done.class;
   }

   public DoneCompiler (final Compiler compiler)
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
         compiler.assembler().merge
         (
            compiler.registers().get_finalize_context_instructions()
         )
      );
      result.add
      (
         compiler.assembler().merge
         (
            compiler.registers().get_leave_context_instructions()
         )
      );
   }
}
