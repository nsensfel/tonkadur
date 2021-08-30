package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.ArrayList;
import java.util.List;

import tonkadur.fate.v1.lang.instruction.generic.SequenceVariableCall;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class SequenceVariableCallCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return SequenceVariableCall.class;
   }

   public SequenceVariableCallCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final SequenceVariableCall source;
      final ComputationCompiler sequence_cc;
      final List<ComputationCompiler> parameter_ccs;
      final List<Computation> parameters;

      final String return_to_label;

      source = (SequenceVariableCall) instruction;

      return_to_label =
         compiler.assembler().generate_label("<seq_call#return_to>");

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

      result.add
      (
         compiler.assembler().mark_after
         (
            compiler.assembler().merge
            (
               compiler.registers().get_visit_context_instructions
               (
                  sequence_cc.get_computation(),
                  compiler.assembler().get_label_constant(return_to_label)
               )
            ),
            return_to_label
         )
      );

      sequence_cc.release_registers(result);

      for (final ComputationCompiler cc: parameter_ccs)
      {
         cc.release_registers(result);
      }
   }
}
