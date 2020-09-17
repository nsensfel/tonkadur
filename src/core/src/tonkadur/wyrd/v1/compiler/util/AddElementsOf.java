package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class AddElementsOf
{
   /* Utility Class */
   private AddElementsOf () {}

   /* Uses Durstenfeld's shuffling algorithm */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address collection_in,
      final Address collection,
      final boolean to_set
   )
   {
      final List<Instruction> result, while_body;
      final Register iterator, collection_in_size;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      iterator = registers.reserve(Type.INT, result);
      collection_in_size = registers.reserve(Type.INT, result);

      result.add(new SetValue(iterator.get_address(), Constant.ZERO));
      result.add
      (
         new SetValue
         (
            collection_in_size.get_address(),
            new Size(collection_in)
         )
      );

      while_body.add
      (
         AddElement.generate
         (
            registers,
            assembler,
            new ValueOf
            (
               new RelativeAddress
               (
                  collection_in,
                  new Cast(iterator.get_value(), Type.STRING),
                  (
                     (MapType) collection_in.get_target_type()
                  ).get_member_type()
               )
            ),
            collection,
            to_set
         )
      );

      while_body.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.plus(iterator.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.less_than
            (
               iterator.get_value(),
               collection_in_size.get_value()
            ),
            assembler.merge(while_body)
         )
      );

      registers.release(iterator, result);
      registers.release(collection_in_size, result);

      return assembler.merge(result);
   }
}
