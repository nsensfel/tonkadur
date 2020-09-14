package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class CreateCons
{
   /* Utility Class */
   private CreateCons () {}

   /*
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address target,
      final Computation car_value,
      final Computation cdr_value
   )
   {
      final Address car_address, cdr_address;
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      car_address =
         new RelativeAddress
         (
            target,
            new Constant(Type.STRING, "0"),
            car_value.get_type()
         );

      cdr_address =
         new RelativeAddress
         (
            target,
            new Constant(Type.STRING, "1"),
            car_value.get_type()
         );

      result.add(new Initialize(car_address));
      result.add(new Initialize(cdr_address));

      result.add(new SetValue(car_address, car_value));
      result.add(new SetValue(cdr_address, cdr_value));

      return assembler.merge(result);
   }
}
