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

public class ReverseList
{
   /* Utility Class */
   private ReverseList () {}

   /*
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable E .buffer
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
    *    (set .bot (+ 1 (var .bot)))
    *    (set .top (- 1 (var .top)))
    * )
    */
   public static Instruction generate
   (
      final AnonymousVariableManager anonymous_variables,
      final InstructionManager assembler,
      final Computation collection_size,
      final Ref collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Ref buffer, top, bot;
      final Computation value_of_top, value_of_bot;
      final Ref collection_at_top, collection_at_bot;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      buffer = anonymous_variables.reserve(element_type);
      top = anonymous_variables.reserve(Type.INT);
      bot = anonymous_variables.reserve(Type.INT);

      value_of_top = new ValueOf(top);
      value_of_bot = new ValueOf(bot);

      collection_at_top =
         new RelativeRef
         (
            collection,
            new Cast(value_of_top, Type.STRING),
            element_type
         );

      collection_at_bot =
         new RelativeRef
         (
            collection,
            new Cast(value_of_bot, Type.STRING),
            element_type
         );

      /* (set .top (- (collection_size) 1)) */
      result.add
      (
         new SetValue(top, Operation.minus(collection_size, Constant.ONE))
      );

      /* (set .bot 0) */
      result.add(new SetValue(bot, Constant.ZERO));


      /* (set .buffer collection[.top]) */
      while_body.add(new SetValue(buffer, new ValueOf(collection_at_top)));

      /* (set collection[.top] collection[.bot]) */
      while_body.add
      (
         new SetValue(collection_at_top, new ValueOf(collection_at_bot))
      );

      /* (set collection[.bot] .buffer) */
      while_body.add
      (
         new SetValue(collection_at_bot, new ValueOf(buffer))
      );

      /* (set .bot (+ 1 (var .bot))) */
      while_body.add
      (
         new SetValue(bot, Operation.plus(Constant.ONE, value_of_bot))
      );

      /* (set .top (- 1 (var .top))) */
      while_body.add
      (
         new SetValue(top, Operation.minus(Constant.ONE, value_of_top))
      );

      /*
       * (while (< (var .bot) (var .top))
       *    (set .buffer collection[.top])
       *    (set collection[.top] collection[.bot])
       *    (set collection[.bot] .buffer)
       *    (set .bot (+ 1 (var .bot)))
       *    (set .top (- 1 (var .top)))
       * )
       */
      result.add
      (
         While.generate
         (
            anonymous_variables,
            assembler,
            Operation.less_than(value_of_bot, value_of_top),
            assembler.merge(while_body)
         )
      );

      anonymous_variables.release(buffer);
      anonymous_variables.release(top);
      anonymous_variables.release(bot);

      return assembler.merge(result);
   }
}
