package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.Sequence;

import tonkadur.wyrd.v1.lang.World;

public class SequenceCompiler
{
   /* Utility Class */
   private SequenceCompiler () { }

   public static Sequence compile
   (
      tonkadur.fate.v1.lang.Sequence fate_sequence,
      final World wyrd_world
   )
   throws Error
   {
      Sequence result;

      result = wyrd_world.get_sequence(fate_sequence.get_name());

      if (result != null)
      {
         return result;
      }

      /* TODO */
      result = null;

      wyrd_world.add_sequence(result);

      return result;
   }

   public static List<Instruction> compile_main_sequence
   (
      List<tonkadur.fate.v1.lang.meta.Instruction> fate_instruction_list,
      final World wyrd_world
   )
   throws Error
   {
      /* TODO */
      return null;
   }
}
