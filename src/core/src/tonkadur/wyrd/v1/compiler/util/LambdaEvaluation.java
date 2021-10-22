package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.type.DictType;
import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class LambdaEvaluation
{
   /* Utility Class */
   private LambdaEvaluation () {}

   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address lambda_function,
      final Address result_storage,
      final List<Computation> parameters
   )
   {
      final List<Instruction> result;
      final String return_to_label;
      final int invokation_parameters_count;
      final Computation lambda_line;
      final List<Computation> side_channel_params;
      final Register lambda_data_storage;

      lambda_line =
         new ValueOf
         (
            new RelativeAddress
            (
               lambda_function,
               Constant.string_value("l"),
               Type.INT
            )
         );

      result = new ArrayList<Instruction>();
      side_channel_params = new ArrayList<Computation>();

      lambda_data_storage = registers.reserve(DictType.WILD, result);

      return_to_label = assembler.generate_label("<lambda_eval#return_to>");

      result.add
      (
         AddParametersToLambda.generate
         (
            registers,
            assembler,
            lambda_function,
            lambda_data_storage.get_address(),
            parameters,
            0
         )
      );

      side_channel_params.add(result_storage);
      side_channel_params.add(lambda_data_storage.get_address());

      result.addAll(registers.store_parameters(side_channel_params));

      result.add
      (
         assembler.mark_after
         (
            assembler.merge
            (
               registers.get_visit_context_instructions
               (
                  lambda_line,
                  assembler.get_label_constant(return_to_label)
               )
            ),
            return_to_label
         )
      );

      registers.release(lambda_data_storage, result);

      return assembler.merge(result);
   }
}
