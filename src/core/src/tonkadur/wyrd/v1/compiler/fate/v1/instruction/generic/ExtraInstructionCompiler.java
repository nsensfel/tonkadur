package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.ExtraInstruction;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class ExtraInstructionCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return ExtraInstruction.class;
   }

   public ExtraInstructionCompiler (final Compiler compiler)
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
      final ExtraInstruction source;
      final List<ComputationCompiler> cc_list;
      final List<Computation> parameters;

      source = (ExtraInstruction) instruction;

      cc_list = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_computation:
            source.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         fate_computation.get_visited_by(cc);

         if (cc.has_init())
         {
            result.add(cc.get_init());
         }

         cc_list.add(cc);
         parameters.add(cc.get_computation());
      }

      result.add
      (
         new tonkadur.wyrd.v1.lang.instruction.ExtraInstruction
         (
            source.get_instruction_name(),
            parameters
         )
      );

      for (final ComputationCompiler cc: cc_list)
      {
         cc.release_registers(result);
      }
   }
}
