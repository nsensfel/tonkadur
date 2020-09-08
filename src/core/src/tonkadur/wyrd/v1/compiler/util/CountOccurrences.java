package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

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
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation target,
      final Computation collection_size,
      final Address collection,
      final Address count
   )
   {
      final List<Instruction> result, while_body;
      final Type target_type;
      final Register index;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      target_type = target.get_type();

      index = registers.reserve(Type.INT, result);

      result.add(new SetValue(count, Constant.ZERO));
      result.add(new SetValue(index.get_address(), collection_size));

      while_body.add
      (
         new SetValue
         (
            index.get_address(),
            Operation.minus(index.get_value(), Constant.ONE)
         )
      );
      while_body.add
      (
         If.generate
         (
            registers,
            assembler,
            Operation.equals
            (
               new ValueOf
               (
                  new RelativeAddress
                  (
                     collection,
                     new Cast(index.get_value(), Type.STRING),
                     target_type
                  )
               ),
               target
            ),
            new SetValue
            (
               count,
               Operation.plus(new ValueOf(count), Constant.ONE)
            )
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.greater_than(index.get_value(), Constant.ZERO),
            assembler.merge(while_body)
         )
      );

      registers.release(index, result);

      return assembler.merge(result);
   }
}
