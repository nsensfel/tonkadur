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
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

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
    * (set result_index 0)
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
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation target,
      final Computation collection_size,
      final Address collection,
      final Address result_was_found,
      final Address result_index
   )
   {
      final List<Instruction> result, while_body;
      final Register bot, top, midval;
      final Type element_type;
      final Computation value_of_result_index;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type = target.get_type();

      bot = registers.reserve(Type.INT);
      top = registers.reserve(Type.INT);
      midval = registers.reserve(element_type);

      value_of_result_index = new ValueOf(result_index);

      result.add(new SetValue(result_index, Constant.ZERO));
      result.add(new SetValue(result_was_found, Constant.FALSE));
      result.add(new SetValue(bot.get_address(), Constant.ZERO));
      result.add
      (
         new SetValue
         (
            top.get_address(),
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
               bot.get_value(),
               new Cast
               (
                  Operation.divide
                  (
                     new Cast
                     (
                        Operation.minus(top.get_value(), bot.get_value()),
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
            midval.get_address(),
            new ValueOf
            (
               new RelativeAddress
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
            registers,
            assembler,
            Operation.less_than(midval.get_value(), target),
            new SetValue
            (
               bot.get_address(),
               Operation.plus(value_of_result_index, Constant.ONE)
            ),
            IfElse.generate
            (
               registers,
               assembler,
               Operation.greater_than(midval.get_value(), target),
               new SetValue
               (
                  top.get_address(),
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
            registers,
            assembler,
            Operation.and
            (
               Operation.not(new ValueOf(result_was_found)),
               Operation.less_equal_than(bot.get_value(), top.get_value())
            ),
            assembler.merge(while_body)
         )
      );

      /* Without this, you'll replace the value and move it to the right,
       * regardless of where you 'target' stands in relation to it.
       */
      result.add
      (
         If.generate
         (
            registers,
            assembler,
            Operation.and
            (
               Operation.and
               (
                  Operation.not(new ValueOf(result_was_found)),
                  Operation.greater_than(target, midval.get_value())
               ),
               Operation.greater_than(collection_size, Constant.ZERO)
            ),
            new SetValue
            (
               result_index,
               Operation.plus(value_of_result_index, Constant.ONE)
            )
         )
      );

      registers.release(bot);
      registers.release(top);
      registers.release(midval);

      return assembler.merge(result);
   }
}
