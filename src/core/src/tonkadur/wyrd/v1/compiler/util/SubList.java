package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class SubList
{
   /* Utility Class */
   private SubList () {}

   /* Uses Durstenfeld's shuffling algorithm */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation start,
      final Computation end,
      final Address collection,
      final Address result_holder
   )
   {
      final List<Instruction> result, while_body;
      final Register iterator, collection_size, target_index;
      final Address target_address;
      final Type element_type;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      target_index = registers.reserve(Type.INT, result);
      iterator = registers.reserve(Type.INT, result);
      collection_size = registers.reserve(Type.INT, result);

      element_type = ((MapType) collection.get_target_type()).get_member_type();

      target_address =
         new RelativeAddress
         (
            result_holder,
            new Cast(target_index.get_value(), Type.STRING),
            element_type
         );

      result.add(new SetValue(target_index.get_address(), Constant.ZERO));

      result.add(new SetValue(iterator.get_address(), start));
      result.add
      (
         If.generate
         (
            registers,
            assembler,
            Operation.less_than(iterator.get_value(), Constant.ZERO),
            new SetValue(iterator.get_address(), Constant.ZERO)
         )
      );

      result.add
      (
         new SetValue
         (
            collection_size.get_address(),
            Operation.minus(new Size(collection), Constant.ONE)
         )
      );

      result.add
      (
         If.generate
         (
            registers,
            assembler,
            Operation.greater_than(collection_size.get_value(), end),
            new SetValue(collection_size.get_address(), end)
         )
      );


      while_body.add(new Initialize(target_address, element_type));

      while_body.add
      (
         new SetValue
         (
            target_address,
            new ValueOf
            (
               new RelativeAddress
               (
                  collection,
                  new Cast(iterator.get_value(), Type.STRING),
                  element_type
               )
            )
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

      while_body.add
      (
         new SetValue
         (
            target_index.get_address(),
            Operation.plus(target_index.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.less_equal_than
            (
               iterator.get_value(),
               collection_size.get_value()
            ),
            assembler.merge(while_body)
         )
      );

      registers.release(target_index, result);
      registers.release(iterator, result);
      registers.release(collection_size, result);

      return assembler.merge(result);
   }
}
