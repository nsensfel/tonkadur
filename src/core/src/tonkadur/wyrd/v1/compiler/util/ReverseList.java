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

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class ReverseList
{
   /* Utility Class */
   private ReverseList () {}

   /*
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable E .buffer)
    * (declare_variable int .top)
    * (declare_variable int .bot)
    *
    * (set .top (- (collection_size) 1))
    * (set .bot 0)
    *
    * (while (< (var .bot) (var .top))
    *    (set .buffer collection[.top])
    *    (set collection[.top] collection[.bot])
    *    (set collection[.bot] .buffer)
    *    (set .bot (+ (var .bot) 1))
    *    (set .top (- (var .top) 1))
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
      final Register buffer, top, bot;
      final Address collection_at_top, collection_at_bot;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      buffer = registers.reserve(element_type);
      top = registers.reserve(Type.INT);
      bot = registers.reserve(Type.INT);

      collection_at_top =
         new RelativeAddress
         (
            collection,
            new Cast(top.get_value(), Type.STRING),
            element_type
         );

      collection_at_bot =
         new RelativeAddress
         (
            collection,
            new Cast(bot.get_value(), Type.STRING),
            element_type
         );

      /* (set .top (- (collection_size) 1)) */
      result.add
      (
         new SetValue
         (
            top.get_address(),
            Operation.minus(collection_size, Constant.ONE)
         )
      );

      /* (set .bot 0) */
      result.add(new SetValue(bot.get_address(), Constant.ZERO));


      /* (set .buffer collection[.top]) */
      while_body.add
      (
         new SetValue(buffer.get_address(), new ValueOf(collection_at_top))
      );

      /* (set collection[.top] collection[.bot]) */
      while_body.add
      (
         new SetValue(collection_at_top, new ValueOf(collection_at_bot))
      );

      /* (set collection[.bot] .buffer) */
      while_body.add
      (
         new SetValue(collection_at_bot, buffer.get_value())
      );

      /* (set .bot (+ (var .bot) 1)) */
      while_body.add
      (
         new SetValue
         (
            bot.get_address(),
            Operation.plus(bot.get_value(), Constant.ONE)
         )
      );

      /* (set .top (- (var .top) 1)) */
      while_body.add
      (
         new SetValue
         (
            top.get_address(),
            Operation.minus(top.get_value(), Constant.ONE)
         )
      );

      /*
       * (while (< (var .bot) (var .top))
       *    (set .buffer collection[.top])
       *    (set collection[.top] collection[.bot])
       *    (set collection[.bot] .buffer)
       *    (set .bot (+ (var .bot) 1))
       *    (set .top (- (var .top) 1))
       * )
       */
      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.less_than(bot.get_value(), top.get_value()),
            assembler.merge(while_body)
         )
      );

      registers.release(buffer);
      registers.release(top);
      registers.release(bot);

      return assembler.merge(result);
   }
}
