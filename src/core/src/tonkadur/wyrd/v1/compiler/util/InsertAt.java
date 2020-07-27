package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.RelativeRef;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.IfElseInstruction;
import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.While;

public class InsertAt
{
   /* Utility Class */
   private InsertAt () {}

   /*
    * (declare_variable global int index)
    * (Computation <E> element)
    * (Computation <int> collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable int .prev)
    * (declare_variable int .end)
    *
    * (set .end collection_size)
    *
    * (while (> .index .end)
    *    (set .prev (- (val .collection_size) 1))
    *    (set collection[.end] (val collection[.prev]))
    *    (set .end (val .prev))
    * )
    *
    * (set collection[.index] element)
   */
   public static List<Instruction> generate
   (
      final AnonymousVariableManager anonymous_variables,
      final Ref index,
      final Computation element,
      final Computation collection_size,
      final Ref collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Ref prev, end;
      final Computation value_of_prev, value_of_index, value_of_end;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type = element.get_type();

      prev = anonymous_variables.reserve(Type.INT);
      end = anonymous_variables.reserve(Type.INT);

      value_of_prev = new ValueOf(prev);
      value_of_index = new ValueOf(index);
      value_of_end = new ValueOf(end);

      /* (set .end collection_size) */
      result.add(new SetValue(end, collection_size));

      /* (set .prev (- (val .end) 1)) */
      while_body.add
      (
         new SetValue
         (
            prev,
            Operation.minus(value_of_end, new Constant(Type.INT, "1"))
         )
      );

      /* (set collection[.end] (val collection[.prev])) */
      while_body.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection,
               Collections.singletonList
               (
                  new Cast(value_of_end, Type.STRING)
               ),
               element_type
            ),
            new ValueOf
            (
               new RelativeRef
               (
                  collection,
                  Collections.singletonList
                  (
                     new Cast(value_of_prev, Type.STRING)
                  ),
                  element_type
               )
            )
         )
      );

      /* (set .end (val .prev)) */
      while_body.add(new SetValue(end, value_of_prev));

      /*
       * (while (> .index .end)
       *    (set .prev (- (val .collection_size) 1))
       *    (set collection[.end] (val collection[.prev]))
       *    (set .end (val .prev))
       * )
       */
      result.add
      (
         new While(Operation.minus(value_of_index, value_of_end), while_body)
      );

      /* (set collection[.index] element) */
      result.add
      (
         new SetValue
         (
            new RelativeRef
            (
               collection,
               Collections.singletonList
               (
                  new Cast(value_of_index, Type.STRING)
               ),
               element_type
            ),
            element
         )
      );

      anonymous_variables.release(end);
      anonymous_variables.release(prev);

      return result;
   }
}
