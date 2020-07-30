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
import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.RelativeRef;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Remove;

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
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Ref index,
      final Computation collection_size,
      final Ref collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Ref next, end;
      final Computation value_of_index;
      final Computation value_of_next, value_of_end;
      final Computation const_1;
      final Ref collection_at_index;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      next = anonymous_variables.reserve(Type.INT);
      end = anonymous_variables.reserve(Type.INT);

      value_of_index = new ValueOf(index);

      value_of_next = new ValueOf(next);
      value_of_end = new ValueOf(end);

      const_1 = new Constant(Type.INT, "1");

      collection_at_index =
         new RelativeRef
         (
            collection,
            new Cast(value_of_index, Type.STRING),
            element_type
         );

      /* (set .end (- (collection_size) 1) */
      result.add
      (
         new SetValue(end, Operation.minus(collection_size, const_1))
      );

      /* (set .next (+ (val index) 1)) */
      while_body.add
      (
         new SetValue(next, Operation.plus(value_of_end, const_1))
      );

      /* (set collection[index] (val collection[.next])) */
      while_body.add
      (
         new SetValue
         (
            collection_at_index,
            new ValueOf
            (
               new RelativeRef
               (
                  collection,
                  new Cast(value_of_next, Type.STRING),
                  element_type
               )
            )
         )
      );

      /* (set .end (val .next)) */
      while_body.add(new SetValue(index, value_of_next));

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
            anonymous_variables,
            assembler,
            Operation.less_than(value_of_index, value_of_end),
            assembler.merge(while_body)
         )
      );

      /* (remove collection[index]) */
      result.add(new Remove(collection_at_index));

      anonymous_variables.release(end);
      anonymous_variables.release(next);

      return assembler.merge(result);
   }
}
