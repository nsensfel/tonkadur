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
import tonkadur.wyrd.v1.lang.computation.IfElseComputation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class IndexedMergeLambda
{
   /* Utility Class */
   private IndexedMergeLambda () {}

   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address lambda,
      final Address collection_in_a,
      final Address collection_in_b,
      final Address collection_out,
      final boolean to_set
   )
   {
      final List<Instruction> result, while_body;
      final Register iterator, collection_in_size, storage;
      final Register collection_in_b_size;
      final List<Computation> lambda_params;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      lambda_params = new ArrayList<Computation>();

      iterator = registers.reserve(Type.INT, result);
      collection_in_size = registers.reserve(Type.INT, result);
      collection_in_b_size = registers.reserve(Type.INT, result);
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
            new Size(collection_in_a)
         )
      );

      result.add
      (
         new SetValue
         (
            collection_in_b_size.get_address(),
            new Size(collection_in_b)
         )
      );

      result.add
      (
         new SetValue
         (
            collection_in_size.get_address(),
            new IfElseComputation
            (
               Operation.less_than
               (
                  collection_in_b_size.get_value(),
                  collection_in_size.get_value()
               ),
               collection_in_b_size.get_value(),
               collection_in_size.get_value()
            )
         )
      );



      lambda_params.add(iterator.get_value());
      lambda_params.add
      (
         new ValueOf
         (
            new RelativeAddress
            (
               collection_in_a,
               new Cast(iterator.get_value(), Type.STRING),
               ((MapType) collection_in_a.get_target_type()).get_member_type()
            )
         )
      );
      lambda_params.add
      (
         new ValueOf
         (
            new RelativeAddress
            (
               collection_in_b,
               new Cast(iterator.get_value(), Type.STRING),
               ((MapType) collection_in_b.get_target_type()).get_member_type()
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
            lambda_params
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
      registers.release(collection_in_b_size, result);
      registers.release(storage, result);

      return assembler.merge(result);
   }

   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address lambda,
      final Computation default_a,
      final Address collection_in_a,
      final Computation default_b,
      final Address collection_in_b,
      final Address collection_out,
      final boolean to_set
   )
   {
      final List<Instruction> result, while_body, oob_a_body, oob_b_body;
      final Register iterator_a, iterator_b, oob_a, oob_b;
      final Register collection_a_size, collection_b_size;
      final Register storage;
      final List<Computation> lambda_params;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();
      oob_a_body = new ArrayList<Instruction>();
      oob_b_body = new ArrayList<Instruction>();

      lambda_params = new ArrayList<Computation>();

      iterator_a = registers.reserve(Type.INT, result);
      iterator_b = registers.reserve(Type.INT, result);

      oob_a = registers.reserve(Type.BOOL, result);
      oob_b = registers.reserve(Type.BOOL, result);

      collection_a_size = registers.reserve(Type.INT, result);
      collection_b_size = registers.reserve(Type.INT, result);

      storage =
         registers.reserve
         (
            ((MapType) collection_out.get_target_type()).get_member_type(),
            result
         );

      result.add(new SetValue(iterator_a.get_address(), Constant.ZERO));
      result.add(new SetValue(iterator_b.get_address(), Constant.ZERO));

      result.add
      (
         new SetValue
         (
            collection_a_size.get_address(),
            new Size(collection_in_a)
         )
      );

      result.add
      (
         new SetValue
         (
            collection_b_size.get_address(),
            new Size(collection_in_b)
         )
      );

      result.add
      (
         new SetValue
         (
            oob_a.get_address(),
            Operation.greater_equal_than
            (
               iterator_a.get_value(),
               collection_a_size.get_value()
            )
         )
      );

      result.add
      (
         new SetValue
         (
            oob_b.get_address(),
            Operation.greater_equal_than
            (
               iterator_b.get_value(),
               collection_b_size.get_value()
            )
         )
      );



      lambda_params.add(iterator_a.get_value());

      lambda_params.add
      (
         new IfElseComputation
         (
            oob_a.get_value(),
            default_a,
            new ValueOf
            (
               new RelativeAddress
               (
                  collection_in_a,
                  new Cast(iterator_a.get_value(), Type.STRING),
                  (
                     (MapType) collection_in_a.get_target_type()
                  ).get_member_type()
               )
            )
         )
      );

      lambda_params.add(iterator_b.get_value());

      lambda_params.add
      (
         new IfElseComputation
         (
            oob_b.get_value(),
            default_b,
            new ValueOf
            (
               new RelativeAddress
               (
                  collection_in_b,
                  new Cast(iterator_b.get_value(), Type.STRING),
                  (
                     (MapType) collection_in_b.get_target_type()
                  ).get_member_type()
               )
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
            lambda_params
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

      oob_a_body.add
      (
         new SetValue
         (
            iterator_a.get_address(),
            Operation.plus(iterator_a.get_value(), Constant.ONE)
         )
      );

      oob_a_body.add
      (
         new SetValue
         (
            oob_a.get_address(),
            Operation.greater_equal_than
            (
               iterator_a.get_value(),
               collection_a_size.get_value()
            )
         )
      );

      oob_b_body.add
      (
         new SetValue
         (
            iterator_b.get_address(),
            Operation.plus(iterator_b.get_value(), Constant.ONE)
         )
      );

      oob_b_body.add
      (
         new SetValue
         (
            oob_b.get_address(),
            Operation.greater_equal_than
            (
               iterator_b.get_value(),
               collection_b_size.get_value()
            )
         )
      );

      while_body.add
      (
         If.generate
         (
            registers,
            assembler,
            Operation.not(oob_a.get_value()),
            assembler.merge(oob_a_body)
         )
      );

      while_body.add
      (
         If.generate
         (
            registers,
            assembler,
            Operation.not(oob_b.get_value()),
            assembler.merge(oob_b_body)
         )
      );

      result.add
      (
         While.generate
         (
            registers,
            assembler,
            Operation.not(Operation.and(oob_a.get_value(), oob_b.get_value())),
            assembler.merge(while_body)
         )
      );

      registers.release(iterator_a, result);
      registers.release(iterator_b, result);
      registers.release(oob_a, result);
      registers.release(oob_b, result);
      registers.release(collection_a_size, result);
      registers.release(collection_b_size, result);
      registers.release(storage, result);

      return assembler.merge(result);
   }
}
