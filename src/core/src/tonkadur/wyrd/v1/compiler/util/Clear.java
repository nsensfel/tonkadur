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
import tonkadur.wyrd.v1.lang.instruction.Remove;

public class Clear
{
   /* Utility Class */
   private Clear () {}

   /*
    * (Computation int collection_size)
    * (declare_variable global <List E> collection)
    *
    * (declare_variable int .iterator)
    *
    * (set .iterator collection_size)
    *
    * (while (> (var .iterator) 0)
    *    (set .iterator (- (val .iterator) 1))
    *    (remove collection[.iterator])
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
      final Ref iterator;
      final Computation value_of_iterator;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      iterator = anonymous_variables.reserve(Type.INT);

      value_of_iterator = new ValueOf(iterator);

      /* (set .iterator collection_size) */
      result.add(new SetValue(iterator, collection_size));

      /* (set .iterator (- (val .iterator) 1)) */
      while_body.add
      (
         new SetValue
         (
            iterator,
            Operation.minus(value_of_iterator, new Constant(Type.INT, "1"))
         )
      );

      /* (remove collection[.iterator]) */
      while_body.add
      (
         new Remove
         (
            new RelativeRef
            (
               collection,
               new Cast(value_of_iterator, Type.STRING),
               element_type
            )
         )
      );

      result.add
      (
         While.generate
         (
            anonymous_variables,
            assembler,
            Operation.greater_than
            (
               value_of_iterator,
               new Constant(Type.INT, "0")
            ),
            assembler.merge(while_body)
         )
      );

      anonymous_variables.release(iterator);

      return assembler.merge(result);
   }
}
