package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

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

public class CountOccurrences
{

   /* Utility Class */
   private CountOccurrences () {}

   /*
    * (Computation <E> target)
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    * (declare_variable global int count)
    *
    * (declare_variable int .index)
    *
    * (set count 0)
    * (set .index collection_size)
    *
    * <while (> (var .index) 0)
    *    (set .index (- (var index) 1))
    *    <if
    *       (= (var collection[.index]) target)
    *
    *       (set count (+ (var count) 1))
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
      final Ref count
   )
   {
      final List<Instruction> result, while_body;
      final Type target_type;
      final Ref index;
      final Computation value_of_index;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      target_type = target.get_type();

      index = anonymous_variables.reserve(Type.INT);

      value_of_index = new ValueOf(index);

      result.add(new SetValue(count, Constant.ZERO));
      result.add(new SetValue(index, collection_size));

      while_body.add(new SetValue(index, Operation.minus(index, Constant.ONE)));
      while_body.add
      (
         If.generate
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
                     new Cast(value_of_index, Type.STRING),
                     target_type
                  )
               ),
               target
            ),
            new SetValue(count, Operation.plus(count, Constant.ONE))
         )
      );

      result.add
      (
         While.generate
         (
            anonymous_variables,
            assembler,
            Operation.greater_than(value_of_index, Constant.ZERO),
            assembler.merge(while_body)
         )
      );

      return assembler.merge(result);
   }
}
