package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Remove;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class Sort
{
   /* Utility Class */
   private Sort () {}

   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address lambda_fun,
      final Address collection,
      final Address sorted_result_holder
   )
   {
      final Type element_type;
      final List<Instruction> result;
      final List<Instruction> while_body;
      final Register result_was_found, result_index, element_to_insert;
      final Register bot, top, midval, cmp;
      final Register collection_size, iterator;

      element_type =
         ((MapType) collection.get_target_type()).get_member_type();

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      collection_size = registers.reserve(Type.INT, result);
      iterator = registers.reserve(Type.INT, result);

      result_was_found = registers.reserve(Type.BOOL, result);
      result_index = registers.reserve(Type.INT, result);
      element_to_insert = registers.reserve(element_type, result);

      bot = registers.reserve(Type.INT, result);
      top = registers.reserve(Type.INT, result);
      midval = registers.reserve(element_type, result);
      cmp = registers.reserve(Type.INT, result);

      /**** INIT DONE *********************************************************/

      result.add(new SetValue(iterator.get_address(), Constant.ZERO));
      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      while_body.add
      (
         new SetValue
         (
            element_to_insert.get_address(),
            new ValueOf
            (
               new RelativeAddress
               (
                  collection,
                  new Cast(iterator.get_value(), Type.STRING),
                  element_type
               )
            )
         )
      );

      while_body.add
      (
         BinarySearch.generate
         (
            registers,
            assembler,
            lambda_fun,
            element_to_insert.get_value(),
            iterator.get_value(),
            sorted_result_holder,
            result_was_found.get_address(),
            result_index.get_address(),
            bot,
            top,
            midval,
            cmp
         )
      );

      while_body.add
      (
         InsertAt.generate
         (
            registers,
            assembler,
            result_index.get_address(),
            element_to_insert.get_value(),
            iterator.get_value(),
            sorted_result_holder
         )
      );

      while_body.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.plus(iterator.get_value(), Constant.ONE)
         )
      );


      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.less_than
            (
               iterator.get_value(),
               collection_size.get_value()
            ),
            assembler.merge(while_body)
         )
      );

      //result.add(new SetValue(collection, sorted_result.get_value()));

      /**** CLEANUP ***********************************************************/

      registers.release(collection_size, result);
      registers.release(iterator, result);

      registers.release(result_was_found, result);
      registers.release(result_index, result);
      registers.release(element_to_insert, result);

      registers.release(bot, result);
      registers.release(top, result);
      registers.release(midval, result);
      registers.release(cmp, result);

      return assembler.merge(result);
   }
}
