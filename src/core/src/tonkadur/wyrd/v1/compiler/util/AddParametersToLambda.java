package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class AddParametersToLambda
{
   /* Utility Class */
   private AddParametersToLambda () {}

   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address parent_lambda,
      final Address result_storage,
      final List<Computation> parameters,
      final int missing_params_count
   )
   {
      final List<Instruction> result;
      final int parameters_size;

      result = new ArrayList<Instruction>();
      parameters_size = parameters.size();

      result.add(new SetValue(result_storage, new ValueOf(parent_lambda)));

      for (int i = 0; i < parameters_size; ++i)
      {
         final Computation param;
         final Address param_storage_address;

         param = parameters.get(i);

         param_storage_address =
            new RelativeAddress
            (
               result_storage,
               Constant.string_value
               (
                  Integer.toString
                  (
                     ((parameters_size - 1) - i)
                     + missing_params_count
                  )
               ),
               param.get_type()
            );

         result.add
         (
            new Initialize
            (
               param_storage_address,
               param.get_type()
            )
         );

         result.add(new SetValue(param_storage_address, param));
      }

      return assembler.merge(result);
   }
}
