package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class RemoveAllOf
{
   /* Utility Class */
   private RemoveAllOf () {}

   /*
    * (Computation <E> element)
    * (Computation <int> collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable int .end)
    * (declare_variable int .index)
    * (declare_variable int .found)
    *
    * (set .index 0)
    * (set .found 0)
    * (set .end (- collection_size 1))
    *
    * <while (< (var .index) (var .end))
    *    ;; while_body0
    *    <if (= element (var collection[.index]))
    *       ;; if_body
    *       (set .found (+ (var .found) 1))
    *       (set .end (- (var .end) 1))
    *    >
    *    <if (> (var .found) 0)
    *       (set
    *          collection[.index]
    *          (var collection[(+ (var .index) (var .found))])
    *       )
    *    >
    *    (set index (+ (val .index) 1))
    * )
    *
    * (while (> (var .found) 0)
    *    ;; while_body1
    *    (remove collection[(+ (var .end) (var .found))])
    *    (set .found (- (var .found) 1))
    * )
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation element,
      final Computation collection_size,
      final Address collection
   )
   {
      final List<Instruction> result;
      final List<Instruction> while_body0, while_body1, if_body;
      final Type element_type;
      final Register index, found, end;
      final Computation value_of_found_greater_than_0;
      final Address collection_at_index;

      result = new ArrayList<Instruction>();

      while_body0 = new ArrayList<Instruction>();
      while_body1 = new ArrayList<Instruction>();
      if_body = new ArrayList<Instruction>();

      element_type = element.get_type();

      index = registers.reserve(Type.INT, result);
      found = registers.reserve(Type.INT, result);
      end = registers.reserve(Type.INT, result);

      value_of_found_greater_than_0 =
         Operation.greater_than(found.get_value(), Constant.ZERO);

      collection_at_index =
         new RelativeAddress
         (
            collection,
            new Cast(index.get_value(), Type.STRING),
            element_type
         );

      /* (set .index 0) */
      result.add(new SetValue(index.get_address(), Constant.ZERO));
      /* (set .found 0) */
      result.add(new SetValue(found.get_address(), Constant.ZERO));

      /* (set .end (- (collection_size) 1) */
      result.add
      (
         new SetValue
         (
            end.get_address(),
            Operation.minus(collection_size, Constant.ONE)
         )
      );

      /*
       * (
       *    (set .found (+ (var .found) 1))
       *    (set .end (- (var .end) 1))
       * )
       */
      if_body.add
      (
         new SetValue
         (
            found.get_address(),
            Operation.plus(found.get_value(), Constant.ONE)
         )
      );
      if_body.add
      (
         new SetValue
         (
            end.get_address(),
            Operation.minus(end.get_value(), Constant.ONE)
         )
      );

      while_body0.add
      (
         /* <if (= element (var collection[.index])) */
         If.generate
         (
            registers,
            assembler,
            Operation.equals(element, new ValueOf(collection_at_index)),
            assembler.merge(if_body)
         )
      );

      /*
       *    <if (> (var .found) 0)
       *       (set
       *          collection[.index]
       *          (var collection[(+ (var .index) (var .found))])
       *       )
       *    >
       */
      while_body0.add
      (
         If.generate
         (
            registers,
            assembler,
            value_of_found_greater_than_0,
            new SetValue
            (
               collection_at_index,
               new ValueOf
               (
                  new RelativeAddress
                  (
                     collection,
                     new Cast
                     (
                        Operation.plus(index.get_value(), found.get_value()),
                        Type.STRING
                     ),
                     element_type
                  )
               )
            )
         )
      );

      /* (set .index (+ (val .index) 1)) */
      while_body0.add
      (
         new SetValue
         (
            index.get_address(),
            Operation.plus(index.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            /* <while (< (var .index) (var .end)) */
            Operation.less_than(index.get_value(), end.get_value()),
            assembler.merge(while_body0)
         )
      );

      /* (remove collection[(+ (var .end) (var .found))]) */
      while_body1.add
      (
         new Remove
         (
            new RelativeAddress
            (
               collection,
               new Cast
               (
                  Operation.plus(end.get_value(), found.get_value()),
                  Type.STRING
               ),
               element_type
            )
         )
      );
      /* (set .found (- (var .found) 1)) */
      while_body1.add
      (
         new SetValue
         (
            found.get_address(),
            Operation.minus(found.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            /* (while (> (var .found) 0) */
            value_of_found_greater_than_0,
            assembler.merge(while_body1)
         )
      );

      registers.release(index, result);
      registers.release(found, result);
      registers.release(end, result);

      return assembler.merge(result);
   }
}
