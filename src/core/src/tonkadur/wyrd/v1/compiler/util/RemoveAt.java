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

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Remove;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class RemoveAt
{
   /* Utility Class */
   private RemoveAt () {}

   /*
    * (declare_variable global int index)
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable int .next)
    * (declare_variable int .end)
    *
    * (set .end (- (collection_size) 1))
    *
    * (while (< (var index) (var .end))
    *    (set .next (+ (val index) 1))
    *    (set collection[index] (val collection[.next]))
    *    (set index (val .next))
    * )
    *
    * (remove collection[index])
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address index,
      final Computation collection_size,
      final Address collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Register next, end;
      final Computation value_of_index;
      final Address collection_at_index;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      next = registers.reserve(Type.INT);
      end = registers.reserve(Type.INT);

      value_of_index = new ValueOf(index);

      collection_at_index =
         new RelativeAddress
         (
            collection,
            new Cast(value_of_index, Type.STRING),
            element_type
         );

      /* (set .end (- (collection_size) 1) */
      result.add
      (
         new SetValue
         (
            end.get_address(),
            Operation.minus(collection_size, Constant.ONE)
         )
      );

      /* (set .next (+ (val index) 1)) */
      while_body.add
      (
         new SetValue
         (
            next.get_address(),
            Operation.plus(value_of_index, Constant.ONE)
         )
      );

      /* (set collection[index] (val collection[.next])) */
      while_body.add
      (
         new SetValue
         (
            collection_at_index,
            new ValueOf
            (
               new RelativeAddress
               (
                  collection,
                  new Cast(next.get_value(), Type.STRING),
                  element_type
               )
            )
         )
      );

      /* (set .end (val .next)) */
      while_body.add(new SetValue(index, next.get_value()));

      /*
       * (while (< (var index) (var .end))
       *    (set .next (+ (val index) 1))
       *    (set collection[index] (val collection[.next]))
       *    (set index (val .next))
       * )
       */
      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.less_than(value_of_index, end.get_value()),
            assembler.merge(while_body)
         )
      );

      /* (remove collection[index]) */
      result.add(new Remove(collection_at_index));

      registers.release(end);
      registers.release(next);

      return assembler.merge(result);
   }
}
