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

public class IndexedPartitionLambda
{
   /* Utility Class */
   private IndexedPartitionLambda () {}

   /* Uses Durstenfeld's shuffling algorithm */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation lambda,
      final Address collection_in,
      final Address collection_out,
      final boolean to_set,
      final List<Computation> extra_params
   )
   {
      final List<Instruction> result, while_body, remove_instructions;
      final Register iterator, index_storage, collection_size, storage;
      final Computation iterator_target;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();
      remove_instructions = new ArrayList<Instruction>();

      iterator = registers.reserve(Type.INT, result);
      index_storage = registers.reserve(Type.INT, result);
      collection_size = registers.reserve(Type.INT, result);
      storage = registers.reserve(Type.BOOL, result);

      iterator_target =
         new ValueOf
         (
            new RelativeAddress
            (
               collection_in,
               new Cast(iterator.get_value(), Type.STRING),
               ((MapType) collection_in.get_target_type()).get_member_type()
            )
         );

      result.add(new SetValue(iterator.get_address(), Constant.ZERO));
      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection_in))
      );

      extra_params.add(0, iterator_target);
      extra_params.add(0, iterator.get_value());

      remove_instructions.add
      (
         AddElement.generate
         (
            registers,
            assembler,
            iterator_target,
            collection_out,
            to_set
         )
      );

      remove_instructions.add
      (
         new SetValue(index_storage.get_address(), iterator.get_value())
      );

      remove_instructions.add
      (
         RemoveAt.generate
         (
            registers,
            assembler,
            index_storage.get_address(),
            collection_size.get_value(),
            collection_in
         )
      );

      remove_instructions.add
      (
         new SetValue
         (
            collection_size.get_address(),
            Operation.minus(collection_size.get_value(), Constant.ONE)
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
            extra_params
         )
      );

      while_body.add
      (
         IfElse.generate
         (
            registers,
            assembler,
            Operation.not(storage.get_value()),
            assembler.merge(remove_instructions),
            new SetValue
            (
               iterator.get_address(),
               Operation.plus(iterator.get_value(), Constant.ONE)
            )
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

      registers.release(iterator, result);
      registers.release(index_storage, result);
      registers.release(collection_size, result);
      registers.release(storage, result);

      return assembler.merge(result);
   }
}
