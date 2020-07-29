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

import tonkadur.wyrd.v1.lang.instruction.SetValue;

public class IterativeSearch
{

   /* Utility Class */
   private IterativeSearch () {}

   /*
    * (Computation <E> target)
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    * (declare_variable global boolean result_found)
    * (declare_variable global index result_index)
    *
    * (set result_found false)
    * (set result_index (- collection_size 1))
    *
    * <while
    *    (and
    *       (not (var result_found))
    *       (>= (var result_index) 0)
    *    )
    *
    *    <ifelse
    *       (= (var collection[result_index]) target)
    *
    *       (set result_found true)
    *
    *       (set result_index (- (var result_index) 1))
    *    >
    * >
    */
   public static Instruction generate
   (
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Computation target,
      final Computation collection_size,
      final Ref collection,
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
         While.generate
         (
            anonymous_variables,
            assembler,
            Operation.and
            (
               Operation.not(new ValueOf(result_was_found)),
               Operation.greater_equal_than
               (
                  value_of_result_index,
                  new Constant(Type.INT, "0")
               )
            ),
            /*
             * <ifelse
             *    (= (var collection[result_index]) target)
             *
             *    (set result_found true)
             *
             *    (set result_index (- (var result_index) 1))
             * >
             */
            IfElse.generate
            (
               anonymous_variables,
               assembler,
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
               new SetValue(result_was_found, Constant.TRUE),
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
      );

      return assembler.merge(result);
   }
}