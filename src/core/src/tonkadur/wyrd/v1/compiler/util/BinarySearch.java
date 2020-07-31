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
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

public class BinarySearch
{

   /* Utility Class */
   private BinarySearch () {}

   /*
    * (Computation <E> element)
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    * (declare_variable global boolean result_found)
    * (declare_variable global index result_index)
    *
    * (declare_variable int .bot)
    * (declare_variable int .top)
    * (declare_variable <E> .midval)
    *
    * (set result_found false)
    * (set .bot 0)
    * (set .top (- collection_size 1))
    *
    * <while
    *    (and (not (var result_found)) (<= (var .bot) (var .top)))
    *
    *    (set result_index
    *       (+
    *          (var .bot)
    *          (cast int
    *             (/
    *                (cast float
    *                   (- (var .top) (var .bot))
    *                )
    *                2.0
    *             )
    *          )
    *       )
    *    )
    *    (set .midval (var collection[.result_index]))
    *    <ifelse
    *       (< (var .midval) element)
    *
    *       (set .bot (+ (var result_index) 1))
    *
    *       <ifelse
    *          (> (var .midval) element)
    *
    *          (set .top (- (var result_index) 1))
    *
    *          (set result_found true)
    *       >
    *    >
    * )
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
      final List<Instruction> result, while_body;
      final Ref bot, top, midval;
      final Type element_type;
      final Computation value_of_result_index;
      final Computation value_of_bot, value_of_top, value_of_midval;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type = target.get_type();

      bot = anonymous_variables.reserve(Type.INT);
      top = anonymous_variables.reserve(Type.INT);
      midval = anonymous_variables.reserve(element_type);

      value_of_result_index = new ValueOf(result_index);

      value_of_bot = new ValueOf(bot);
      value_of_top = new ValueOf(top);
      value_of_midval = new ValueOf(midval);

      result.add(new SetValue(result_was_found, Constant.FALSE));
      result.add(new SetValue(bot, Constant.ZERO));
      result.add
      (
         new SetValue
         (
            top,
            Operation.minus(collection_size, Constant.ONE)
         )
      );

      /*
       *    (set result_index
       *       (+
       *          (var .bot)
       *          (cast int
       *             (/
       *                (cast float
       *                   (- (var .top) (var .bot))
       *                )
       *                2.0
       *             )
       *          )
       *       )
       *    )
       */
      while_body.add
      (
         new SetValue
         (
            result_index,
            Operation.plus
            (
               value_of_bot,
               new Cast
               (
                  Operation.divide
                  (
                     new Cast
                     (
                        Operation.minus(value_of_top, value_of_bot),
                        Type.FLOAT
                     ),
                     new Constant(Type.FLOAT, "2.0")
                  ),
                  Type.INT
               )
            )
         )
      );

      /* (set .midval (var collection[.result_index])) */
      while_body.add
      (
         new SetValue
         (
            midval,
            new ValueOf
            (
               new RelativeRef
               (
                  collection,
                  new Cast(value_of_result_index, Type.STRING),
                  element_type
               )
            )
         )
      );

      /*
       *    <ifelse
       *       (< (var .midval) element)
       *
       *       (set .bot (+ (var result_index) 1))
       *
       *       <ifelse
       *          (> (var .midval) element)
       *
       *          (set .top (- (var result_index) 1))
       *
       *          (set result_found true)
       *       >
       *    >
       */
      while_body.add
      (
         IfElse.generate
         (
            anonymous_variables,
            assembler,
            Operation.less_than(value_of_midval, target),
            new SetValue
            (
               bot,
               Operation.plus(value_of_result_index, Constant.ONE)
            ),
            IfElse.generate
            (
               anonymous_variables,
               assembler,
               Operation.greater_than(value_of_midval, target),
               new SetValue
               (
                  top,
                  Operation.minus(value_of_result_index, Constant.ONE)
               ),
               new SetValue(result_was_found, Constant.TRUE)
            )
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
               Operation.less_equal_than(value_of_bot, value_of_top)
            ),
            assembler.merge(while_body)
         )
      );

      anonymous_variables.release(bot);
      anonymous_variables.release(top);
      anonymous_variables.release(midval);

      return assembler.merge(result);
   }
}
