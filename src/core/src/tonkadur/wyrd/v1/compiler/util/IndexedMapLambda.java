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
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class IndexedMapLambda
{
   /* Utility Class */
   private IndexedMapLambda () {}

   /* Uses Durstenfeld's shuffling algorithm */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation lambda,
      final Address collection_in,
      final Address collection_out,
      final boolean to_set
   )
   {
      final List<Computation> params;
      final List<Instruction> result, while_body;
      final Register iterator, collection_in_size, storage;

      params = new ArrayList<Computation>();
      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      iterator = registers.reserve(Type.INT, result);
      collection_in_size = registers.reserve(Type.INT, result);
      storage =
         registers.reserve
         (
            ((MapType) collection_out.get_target_type()).get_member_type(),
            result
         );

      result.add(new SetValue(iterator.get_address(), Constant.ZERO));
      result.add
      (
         new SetValue
         (
            collection_in_size.get_address(),
            new Size(collection_in)
         )
      );

      params.add(iterator.get_value());

      params.add
      (
         new ValueOf
         (
            new RelativeAddress
            (
               collection_in,
               new Cast(iterator.get_value(), Type.STRING),
               ((MapType) collection_in.get_target_type()).get_member_type()
            )
         )
      );

      while_body.add
      (
         LambdaEvaluation.generate
         (
            registers,
            assembler,
            lambda,
            /* Can't put it in the target collection directly, since that may
             * be a set.
             */
            storage.get_address(),
            params
         )
      );

      while_body.add
      (
         AddElement.generate
         (
            registers,
            assembler,
            storage.get_value(),
            collection_out,
            to_set
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
               collection_in_size.get_value()
            ),
            assembler.merge(while_body)
         )
      );

      registers.release(iterator, result);
      registers.release(collection_in_size, result);
      registers.release(storage, result);

      return assembler.merge(result);
   }
}
