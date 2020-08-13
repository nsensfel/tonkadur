package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Remove;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class Clear
{
   /* Utility Class */
   private Clear () {}

   /*
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable int .iterator)
    *
    * (set .iterator collection_size)
    *
    * (while (> (var .iterator) 0)
    *    (set .iterator (- (val .iterator) 1))
    *    (remove collection[.iterator])
    * )
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation collection_size,
      final Address collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Register iterator;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      iterator = registers.reserve(Type.INT);

      value_of_iterator = new ValueOf(iterator);

      /* (set .iterator collection_size) */
      result.add(new SetValue(iterator.get_address(), collection_size));

      /* (set .iterator (- (val .iterator) 1)) */
      while_body.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.minus(iterator.get_value(), Constant.ONE)
         )
      );

      /* (remove collection[.iterator]) */
      while_body.add
      (
         new Remove
         (
            new RelativeAddress
            (
               collection,
               new Cast(iterator.get_value(), Type.STRING),
               element_type
            )
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

      registers.release(iterator);

      return assembler.merge(result);
   }
}
