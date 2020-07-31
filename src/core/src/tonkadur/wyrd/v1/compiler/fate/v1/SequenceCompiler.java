package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.World;

public class SequenceCompiler
{
   /* Utility Class */
   private SequenceCompiler () { }

   public static void compile
   (
      final Compiler compiler,
      final tonkadur.fate.v1.lang.Sequence fate_sequence
   )
   throws Throwable
   {
      final InstructionCompiler ic;

      ic = new InstructionCompiler(compiler);

      compiler.assembler().add_fixed_name_label(fate_sequence.get_name());

      System.out.println
      (
         "[D] Compiling sequence '"
         + fate_sequence.get_name()
         + "'..."
      );

      fate_sequence.get_root().get_visited_by(ic);

      compiler.world().add_sequence_label
      (
         fate_sequence.get_name(),
         (compiler.world().get_current_line() + 1)
      );

      compiler.assembler().handle_adding_instruction
      (
         compiler.assembler().mark
         (
            fate_sequence.get_name(),
            ic.get_result()
         ),
         compiler.world()
      );
   }

   public static void compile_main_sequence
   (
      final Compiler compiler,
      final List<tonkadur.fate.v1.lang.meta.Instruction> fate_instruction_list
   )
   throws Throwable
   {
      final InstructionCompiler ic;

      ic = new InstructionCompiler(compiler);

      System.out.println("[D] Compiling main sequence...");

      for
      (
         final tonkadur.fate.v1.lang.meta.Instruction fate_instruction:
            fate_instruction_list
      )
      {
         fate_instruction.get_visited_by(ic);
      }

      compiler.assembler().handle_adding_instruction
      (
         ic.get_result(),
         compiler.world()
      );
   }
}
