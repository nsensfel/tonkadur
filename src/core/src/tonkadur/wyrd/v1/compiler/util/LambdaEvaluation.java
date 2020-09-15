package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class LambdaEvaluation
{
   /* Utility Class */
   private LambdaEvaluation () {}

   /* Uses Durstenfeld's shuffling algorithm */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation lambda_function,
      final Address result_storage,
      final List<Computation> parameters
   )
   {
      final List<Instruction> result;
      final String return_to_label;

      result = new ArrayList<Instruction>();

      return_to_label = assembler.generate_label("<lambda_eval#return_to>");

      parameters.add(0, result_storage);

      result.addAll(registers.store_parameters(parameters));

      result.add
      (
         assembler.mark_after
         (
            assembler.merge
            (
               registers.get_visit_context_instructions
               (
                  lambda_function,
                  assembler.get_label_constant(return_to_label)
               )
            ),
            return_to_label
         )
      );

      return assembler.merge(result);
   }
}
