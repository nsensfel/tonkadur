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

public class IndexedFilterLambda
{
   /* Utility Class */
   private IndexedFilterLambda () {}

   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address lambda,
      final Address collection
   )
   {
      final List<Instruction> result, while_body, remove_instructions;
      final Register iterator, index_storage, collection_size, storage;
      final Register index_counter;
      final List<Computation> lambda_params;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();
      remove_instructions = new ArrayList<Instruction>();

      lambda_params = new ArrayList<Computation>();

      iterator = registers.reserve(Type.INT, result);
      index_counter = registers.reserve(Type.INT, result);
      index_storage = registers.reserve(Type.INT, result);
      collection_size = registers.reserve(Type.INT, result);
      storage = registers.reserve(Type.BOOL, result);

      result.add(new SetValue(iterator.get_address(), Constant.ZERO));
      result.add(new SetValue(index_counter.get_address(), Constant.ZERO));
      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      lambda_params.add(index_counter.get_value());
      lambda_params.add
      (
         new ValueOf
         (
            new RelativeAddress
            (
               collection,
               new Cast(iterator.get_value(), Type.STRING),
               ((MapType) collection.get_target_type()).get_member_type()
            )
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
            collection
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
            lambda_params
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
            index_counter.get_address(),
            Operation.plus(index_counter.get_value(), Constant.ONE)
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
      registers.release(index_counter, result);
      registers.release(collection_size, result);
      registers.release(storage, result);

      return assembler.merge(result);
   }
}
