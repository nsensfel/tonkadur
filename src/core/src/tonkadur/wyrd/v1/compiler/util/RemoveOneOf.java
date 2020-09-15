package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class RemoveOneOf
{
   /* Utility Class */
   private RemoveOneOf () {}
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Computation element,
      final Address collection,
      final boolean is_set
   )
   {
      final Register collection_size, found, index;
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      index = registers.reserve(Type.INT, result);
      collection_size = registers.reserve(Type.INT, result);
      found = registers.reserve(Type.BOOL, result);

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      if (is_set)
      {
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
      }
      else
      {
         result.add
         (
            IterativeSearch.generate
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
      }

      result.add
      (
         If.generate
         (
            registers,
            assembler,
            found.get_value(),
            RemoveAt.generate
            (
               registers,
               assembler,
               index.get_address(),
               collection_size.get_value(),
               collection
            )
         )
      );

      registers.release(index, result);
      registers.release(collection_size, result);
      registers.release(found, result);

      return assembler.merge(result);
   }
}
