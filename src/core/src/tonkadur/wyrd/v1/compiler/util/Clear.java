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

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Remove;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;

public class Clear
{
   /* Utility Class */
   private Clear () {}

   /*
    * (declare_variable global <List E> collection)
    *
    * (declare_variable <List E> .clean_value)
    *
    * (set collection .clean_value)
    */
   public static Instruction generate
   (
      final RegisterManager registers,
      final InstructionManager assembler,
      final Address collection
   )
   {
      final List<Instruction> result;
      final Register clean_value;

      result = new ArrayList<Instruction>();

      clean_value = registers.reserve(collection.get_target_type(), result);

      result.add(new SetValue(collection, clean_value.get_value()));

      registers.release(clean_value, result);

      return assembler.merge(result);
   }
}
