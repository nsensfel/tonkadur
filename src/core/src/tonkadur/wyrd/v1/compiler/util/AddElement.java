package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class AddElement
{
   /* Utility Class */
   private AddElement () {}
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation element,
      final Address collection,
      final boolean is_set
   )
   {
      final Register collection_size;
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      collection_size = registers.reserve(Type.INT, result);

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );


      if (is_set)
      {
         final Register found, index;

         index = registers.reserve(Type.INT, result);
         found = registers.reserve(Type.BOOL, result);

         result.add
         (
            BinarySearch.generate
            (
               registers,
               assembler,
               element,
               collection_size.get_value(),
               collection,
               found.get_address(),
               index.get_address()
            )
         );

         result.add
         (
            If.generate
            (
               registers,
               assembler,
               Operation.not(found.get_value()),
               InsertAt.generate
               (
                  registers,
                  assembler,
                  index.get_address(),
                  element,
                  collection_size.get_value(),
                  collection
               )
            )
         );

         registers.release(index, result);
         registers.release(found, result);
      }
      else
      {
         final Address new_elem_addr;

         new_elem_addr =
            new RelativeAddress
            (
               collection,
               new Cast(collection_size.get_value(), Type.STRING),
               element.get_type()
            );

         result.add(new Initialize(new_elem_addr));
         result.add(new SetValue(new_elem_addr, element));
      }

      registers.release(collection_size, result);

      return assembler.merge(result);
   }
}
