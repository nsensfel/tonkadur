package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.SetRandom;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class Shuffle
{
   /* Utility Class */
   private Shuffle () {}

   /* Uses Durstenfeld's shuffling algorithm */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Register iterator, target_index;
      final Register storage;
      final Address a_i, a_j;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      iterator = registers.reserve(Type.INT, result);
      target_index = registers.reserve(Type.INT, result);
      storage = registers.reserve(element_type, result);

      a_i =
         new RelativeAddress
         (
            collection,
            new Cast(iterator.get_value(), Type.STRING),
            element_type
         );

      a_j =
         new RelativeAddress
         (
            collection,
            new Cast(target_index.get_value(), Type.STRING),
            element_type
         );

      while_body.add
      (
         new SetRandom
         (
            target_index.get_address(),
            Constant.ZERO,
            iterator.get_value()
         )
      );

      while_body.add(new SetValue(storage.get_address(), new ValueOf(a_i)));
      while_body.add(new SetValue(a_i, new ValueOf(a_j)));
      while_body.add(new SetValue(a_j, storage.get_value()));

      while_body.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.minus(iterator.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.minus(new Size(collection), Constant.ONE)
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.greater_than(iterator.get_value(), Constant.ZERO),
            assembler.merge(while_body)
         )
      );

      registers.release(iterator, result);
      registers.release(target_index, result);
      registers.release(storage, result);

      return assembler.merge(result);
   }
}
