package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Free;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.instruction.Remove;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;


public class FreeCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Free.class;
   }

   public FreeCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Free source;
      final ComputationCompiler cc;
      final Address target;

      source = (Free) instruction;

      cc = new ComputationCompiler(compiler);

      source.get_reference().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      target = cc.get_address();

      if (target == null)
      {
         System.err.println
         (
            "[P] Argument in (free "
            + source.get_reference()
            + ") did not compile to a address."
         );

         System.exit(-1);
      }

      result.add(new Remove(target));

      cc.release_registers(result);
   }
}
