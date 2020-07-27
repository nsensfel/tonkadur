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

import tonkadur.wyrd.v1.lang.instruction.IfElseInstruction;
import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.While;

public class IterativeSearch
{

   /* Utility Class */
   private IterativeSearch () {}

   /*
    * (Computation <E> target)
    * (declare_variable global <List E> collection)
    * (Computation int collection_size)
    * (declare_variable global boolean result_found)
    * (declare_variable global index result_index)
    *
    * (set result_found false)
    * (set result_index (- collection_size 1))
    *
    * (while
    *    (and
    *       (not (var result_found))
    *       (>= (var result_index) 0)
    *    )
    *    (ifelse (= (var collection[result_index]) target)
    *       (set result_found true)
    *       (set result_index (- (var result_index) 1))
    *    )
    * )
    */
   public static List<Instruction> generate
   (
      final AnonymousVariableManager anonymous_variables,
      final Computation target,
      final Ref collection,
      final Computation collection_size,
      final Ref result_was_found,
      final Ref result_index
   )
   {
      final List<Instruction> result;
      final Type target_type;
      final Computation value_of_result_index;

      result = new ArrayList<Instruction>();

      target_type = target.get_type();

      value_of_result_index = new ValueOf(result_index);

      result.add(new SetValue(result_was_found, Constant.FALSE));
      result.add
      (
         new SetValue
         (
            result_index,
            Operation.minus(collection_size, new Constant(Type.INT, "0"))
         )
      );

      result.add
      (
         new While
         (
            Operation.and
            (
               Operation.not(new ValueOf(result_was_found)),
               Operation.greater_equal_than
               (
                  value_of_result_index,
                  new Constant(Type.INT, "0")
               )
            ),
            Collections.singletonList
            (
               /*
                * (ifelse (= (var collection[result_index]) target)
                *    (set result_found true)
                *    (set result_index (- (var result_index) 1))
                * )
                */
               new IfElseInstruction
               (
                  Operation.equals
                  (
                     new ValueOf
                     (
                        new RelativeRef
                        (
                           collection,
                           Collections.singletonList
                           (
                              new Cast(value_of_result_index, Type.STRING)
                           ),
                           target_type
                        )
                     ),
                     target
                  ),
                  Collections.singletonList
                  (
                     new SetValue(result_was_found, Constant.TRUE)
                  ),
                  Collections.singletonList
                  (
                     new SetValue
                     (
                        result_index,
                        Operation.minus
                        (
                           value_of_result_index,
                           new Constant(Type.INT, "1")
                        )
                     )
                  )
               )
            )
         )
      );

      return result;
   }
}
