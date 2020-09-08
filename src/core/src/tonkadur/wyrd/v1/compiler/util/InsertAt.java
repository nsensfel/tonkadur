package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

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
    * <while
    *    (< .index .end)
    *
    *    (set .prev (- (val .collection_size) 1))
    *    (set collection[.end] (val collection[.prev]))
    *    (set .end (val .prev))
    * >
    *
    * (set collection[.index] element)
   */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address index,
      final Computation element,
      final Computation collection_size,
      final Address collection
   )
   {
      final List<Instruction> result, while_body;
      final Type element_type;
      final Register prev, end;
      final Computation value_of_index;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type = element.get_type();

      prev = registers.reserve(Type.INT, result);
      end = registers.reserve(Type.INT, result);

      value_of_index = new ValueOf(index);


      /* (set .end collection_size) */
      result.add(new SetValue(end.get_address(), collection_size));

      /*
       * Increasing the size of the list by one.
       * This *does* increase the value of collection_size, so don't do it
       * before setting .end.
       */
      result.add
      (
         new Initialize
         (
            new RelativeAddress
            (
               collection,
               new Cast(collection_size, Type.STRING),
               element_type
            ),
            element_type
         )
      );

      /* (set .prev (- (val .end) 1)) */
      while_body.add
      (
         new SetValue
         (
            prev.get_address(),
            Operation.minus(end.get_value(), Constant.ONE)
         )
      );

      /* (set collection[.end] (val collection[.prev])) */
      while_body.add
      (
         new SetValue
         (
            new RelativeAddress
            (
               collection,
               new Cast(end.get_value(), Type.STRING),
               element_type
            ),
            new ValueOf
            (
               new RelativeAddress
               (
                  collection,
                  new Cast(prev.get_value(), Type.STRING),
                  element_type
               )
            )
         )
      );

      /* (set .end (val .prev)) */
      while_body.add(new SetValue(end.get_address(), prev.get_value()));

      /*
       * (while (< .index .end)
       *    (set .prev (- (val .collection_size) 1))
       *    (set collection[.end] (val collection[.prev]))
       *    (set .end (val .prev))
       * )
       */
      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.less_than(value_of_index, end.get_value()),
            assembler.merge(while_body)
         )
      );

      /* (set collection[.index] element) */
      result.add
      (
         new SetValue
         (
            new RelativeAddress
            (
               collection,
               new Cast(value_of_index, Type.STRING),
               element_type
            ),
            element
         )
      );

      registers.release(end, result);
      registers.release(prev, result);

      return assembler.merge(result);
   }
}
