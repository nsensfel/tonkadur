package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.World;
import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.SetPC;

import tonkadur.wyrd.v1.lang.computation.Constant;
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
      final List<Instruction> init_instructions;
      final List<Register> parameters;
      final List<Register> to_be_cleaned;
      final String end_of_sequence;
      final InstructionCompiler ic;

      init_instructions = new ArrayList<Instruction>();
      parameters = new ArrayList<Register>();
      to_be_cleaned = new ArrayList<Register>();
      ic = new InstructionCompiler(compiler);

      end_of_sequence = compiler.assembler().generate_label("<sequence#end>");

      compiler.world().add_sequence_label
      (
         fate_sequence.get_name(),
         (compiler.world().get_current_line() + 1)
      );

      compiler.assembler().add_fixed_name_label(fate_sequence.get_name());

      compiler.registers().create_stackable_context
      (
         fate_sequence.get_name()
      );

      init_instructions.add
      (
         new SetPC(compiler.assembler().get_label_constant(end_of_sequence))
      );

      init_instructions.add
      (
         compiler.assembler().mark
         (
            fate_sequence.get_name(),
            compiler.assembler().merge
            (
               compiler.registers().get_initialize_context_instructions
               (
                  fate_sequence.get_name()
               )
            )
         )
      );

      compiler.registers().push_context(fate_sequence.get_name());

      for
      (
         final tonkadur.fate.v1.lang.Variable param:
            fate_sequence.get_signature()
      )
      {
         final Register r;

         r =
            compiler.registers().reserve
            (
               TypeCompiler.compile(compiler, param.get_type())
            );

         parameters.add(r);
         to_be_cleaned.add(r);

         compiler.registers().bind(param.get_name(), r);
      }

      init_instructions.addAll
      (
         compiler.registers().read_parameters(parameters)
      );

      fate_sequence.get_root().get_visited_by(ic);

      init_instructions.add(ic.get_result());

      init_instructions.addAll
      (
         compiler.registers().get_finalize_context_instructions()
      );

      init_instructions.addAll
      (
         compiler.registers().get_leave_context_instructions()
      );

      for (final Register r: to_be_cleaned)
      {
         compiler.registers().release(r);
      }

      compiler.registers().pop_context();

      compiler.assembler().handle_adding_instruction
      (
         compiler.assembler().mark_after
         (
            compiler.assembler().merge(init_instructions),
            end_of_sequence
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

      compiler.assembler().handle_adding_instruction
      (
         new SetValue
         (
            compiler.registers().get_rand_mode_holder().get_address(),
            Constant.ZERO
         ),
         compiler.world()
      );

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
         compiler.assembler().merge(compiler.registers().pop_initializes()),
         compiler.world()
      );

      compiler.assembler().handle_adding_instruction
      (
         ic.get_result(),
         compiler.world()
      );
   }
}
