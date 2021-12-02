package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.PointerType;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class PopElement
{
   /* Utility Class */
   private PopElement () {}
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address collection,
      final Computation element_holder,
      final boolean is_from_left
   )
   {
      final Register index, collection_size;
      final List<Instruction> result;
      final Computation target_index;

      result = new ArrayList<Instruction>();

      index = registers.reserve(Type.INT, result);
      collection_size = registers.reserve(Type.INT, result);

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );


      if (is_from_left)
      {
         target_index = Constant.ZERO;
      }
      else
      {
         target_index =
            Operation.minus(collection_size.get_value(), Constant.ONE);
      }

      result.add(new SetValue(index.get_address(), target_index));

      result.add
      (
         new SetValue
         (
            element_holder,
            new ValueOf
            (
               new RelativeAddress
               (
                  collection,
                  new Cast(target_index, Type.STRING),
                  ((PointerType) element_holder.get_type()).get_target_type()
               )
            )
         )
      );

      result.add
      (
         RemoveAt.generate
         (
            registers,
            assembler,
            index.get_address(),
            collection_size.get_value(),
            collection
         )
      );

      registers.release(collection_size, result);
      registers.release(index, result);

      return assembler.merge(result);
   }
}
