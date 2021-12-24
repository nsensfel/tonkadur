package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.Allocate;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.GetAllocableAddress;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;


public class AllocateCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return Allocate.class;
   }

   public AllocateCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Allocate source;
      final ComputationCompiler cc;
      final Address target;
      final Type t;

      source = (Allocate) instruction;

      cc = new ComputationCompiler(compiler);

      source.get_target().get_visited_by(cc);

      if (cc.has_init())
      {
         result.add(cc.get_init());
      }

      target = cc.get_address();

      if (target == null)
      {
         System.err.println
         (
            "[P] Argument in (allocate "
            + source.get_target()
            + ") did not compile to a address."
         );

         System.exit(-1);
      }

      t = TypeCompiler.compile(compiler, source.get_allocated_type());

      result.add(new SetValue(target, new GetAllocableAddress(t)));
      result.add(new Initialize(target, t));

      cc.release_registers(result);
   }
}
