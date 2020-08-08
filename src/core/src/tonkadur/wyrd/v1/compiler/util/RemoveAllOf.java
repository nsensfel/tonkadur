package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.RelativeRef;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.SetValue;

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
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Computation element,
      final Computation collection_size,
      final Ref collection
   )
   {
      final List<Instruction> result;
      final List<Instruction> while_body0, while_body1, if_body;
      final Type element_type;
      final Ref index, found, end;
      final Computation value_of_index, value_of_found, value_of_end;
      final Computation value_of_found_greater_than_0;
      final Ref collection_at_index;

      result = new ArrayList<Instruction>();

      while_body0 = new ArrayList<Instruction>();
      while_body1 = new ArrayList<Instruction>();
      if_body = new ArrayList<Instruction>();

      element_type = element.get_type();

      index = anonymous_variables.reserve(Type.INT);
      found = anonymous_variables.reserve(Type.INT);
      end = anonymous_variables.reserve(Type.INT);

      value_of_index = new ValueOf(index);
      value_of_found = new ValueOf(found);
      value_of_end = new ValueOf(end);

      value_of_found_greater_than_0 =
         Operation.greater_than(value_of_found, Constant.ZERO);

      collection_at_index =
         new RelativeRef
         (
            collection,
            new Cast(value_of_index, Type.STRING),
            element_type
         );

      /* (set .index 0) */
      result.add(new SetValue(index, Constant.ZERO));
      /* (set .found 0) */
      result.add(new SetValue(found, Constant.ZERO));

      /* (set .end (- (collection_size) 1) */
      result.add
      (
         new SetValue(end, Operation.minus(collection_size, Constant.ONE))
      );

      /*
       * (
       *    (set .found (+ (var .found) 1))
       *    (set .end (- (var .end) 1))
       * )
       */
      if_body.add
      (
         new SetValue(found, Operation.plus(value_of_found, Constant.ONE))
      );
      if_body.add
      (
         new SetValue(end, Operation.minus(value_of_end, Constant.ONE))
      );

      while_body0.add
      (
         /* <if (= element (var collection[.index])) */
         If.generate
         (
            anonymous_variables,
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
            anonymous_variables,
            assembler,
            value_of_found_greater_than_0,
            new SetValue
            (
               collection_at_index,
               new ValueOf
               (
                  new RelativeRef
                  (
                     collection,
                     new Cast
                     (
                        Operation.plus(value_of_index, value_of_found),
                        Type.STRING
                     ),
                     element_type
                  )
               )
            )
         )
      );

      /* (set index (+ (val .index) 1)) */
      while_body0.add
      (
         new SetValue(index, Operation.plus(value_of_index, Constant.ONE))
      );

      result.add
      (
         While.generate
         (
            anonymous_variables,
            assembler,
            /* <while (< (var .index) (var .end)) */
            Operation.less_than(value_of_index, value_of_end),
            assembler.merge(while_body0)
         )
      );

      /* (remove collection[(+ (var .end) (var .found))]) */
      while_body1.add
      (
         new Remove
         (
            new RelativeRef
            (
               collection,
               new Cast
               (
                  Operation.plus(value_of_end, value_of_found),
                  Type.STRING
               ),
               element_type
            )
         )
      );
      /* (set .found (- (var .found) 1)) */
      while_body1.add
      (
         new SetValue(found, Operation.minus(value_of_found, Constant.ONE))
      );

      result.add
      (
         While.generate
         (
            anonymous_variables,
            assembler,
            /* (while (> (var .found) 0) */
            value_of_found_greater_than_0,
            assembler.merge(while_body1)
         )
      );

      anonymous_variables.release(index);
      anonymous_variables.release(found);
      anonymous_variables.release(end);

      return assembler.merge(result);
   }
}
