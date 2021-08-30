package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.ArrayList;
import java.util.List;

import tonkadur.fate.v1.lang.instruction.generic.SequenceVariableJump;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class SequenceVariableJumpCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return SequenceVariableJump.class;
   }

   public SequenceVariableJumpCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final SequenceVariableJump source;
      final ComputationCompiler sequence_cc;
      final List<ComputationCompiler> parameter_ccs;
      final List<Computation> parameters;

      source = (SequenceVariableJump) instruction;

      sequence_cc = new ComputationCompiler(compiler);
      parameter_ccs = new ArrayList<ComputationCompiler>();
      parameters = new ArrayList<Computation>();

      source.get_sequence().get_visited_by(sequence_cc);

      if (sequence_cc.has_init())
      {
         result.add(sequence_cc.get_init());
      }

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation param:
            source.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         param.get_visited_by(cc);

         if (cc.has_init())
         {
            result.add(cc.get_init());
         }

         parameters.add(cc.get_computation());
         parameter_ccs.add(cc);
      }

      result.addAll(compiler.registers().store_parameters(parameters));

      for (final ComputationCompiler cc: parameter_ccs)
      {
         cc.release_registers(result);
      }

      // Terminate current context
      result.addAll
      (
         compiler.registers().get_finalize_context_instructions()
      );

      result.addAll
      (
         compiler.registers().get_jump_to_context_instructions
         (
            sequence_cc.get_computation()
         )
      );

      sequence_cc.release_registers(result);
   }
}
