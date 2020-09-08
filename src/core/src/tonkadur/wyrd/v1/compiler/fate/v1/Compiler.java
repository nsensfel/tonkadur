package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.compiler.util.registers.RegisterManager;
import tonkadur.wyrd.v1.compiler.util.InstructionManager;

import tonkadur.wyrd.v1.lang.type.DictType;
import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.Register;
import tonkadur.wyrd.v1.lang.World;

public class Compiler
{
   protected final RegisterManager registers;
   protected final InstructionManager assembler;
   protected final World wyrd_world;


   public static World compile
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final World wyrd_world
   )
   throws Throwable
   {
      final Compiler compiler;

      compiler = new Compiler(wyrd_world);

      compiler.assembler().handle_adding_instruction
      (
         compiler.assembler().merge(compiler.registers().pop_initializes()),
         wyrd_world
      );

      compiler.compile_extensions(fate_world);
      compiler.compile_types(fate_world);
      compiler.compile_variables(fate_world);

      compiler.compile_sequences(fate_world);

      compiler.compile_main_sequence(fate_world);

      compiler.add_registers();

      return compiler.wyrd_world;
   }

   protected Compiler (final World wyrd_world)
   {
      this.wyrd_world = wyrd_world;

      registers = new RegisterManager();
      assembler = new InstructionManager();
   }

   protected void compile_extensions
   (
      final tonkadur.fate.v1.lang.World fate_world
   )
   {
      for (final String extension: fate_world.get_required_extensions())
      {
         wyrd_world.add_required_extension(extension);
      }
   }

   protected void compile_types
   (
      final tonkadur.fate.v1.lang.World fate_world
   )
   throws Throwable
   {
      for
      (
         final tonkadur.fate.v1.lang.type.Type type:
            fate_world.types().get_all()
      )
      {
         TypeCompiler.compile(this, type);
      }
   }

   protected void compile_variables
   (
      final tonkadur.fate.v1.lang.World fate_world
   )
   throws Throwable
   {
      final List<Instruction> init_instr;

      init_instr = new ArrayList<Instruction>();

      for
      (
         final tonkadur.fate.v1.lang.Variable variable:
            fate_world.variables().get_all()
      )
      {
         final Type t;
         final Register r;

         t = TypeCompiler.compile(this, variable.get_type());
         r = registers.register(t, variable.get_name());
      }

      this.assembler().handle_adding_instruction
      (
         this.assembler().merge(init_instr),
         this.world()
      );
   }

   protected void compile_sequences
   (
      final tonkadur.fate.v1.lang.World fate_world
   )
   throws Throwable
   {
      for
      (
         final tonkadur.fate.v1.lang.Sequence sequence:
            fate_world.sequences().get_all()
      )
      {
         SequenceCompiler.compile(this, sequence);
      }
   }

   protected void compile_main_sequence
   (
      final tonkadur.fate.v1.lang.World fate_world
   )
   throws Throwable
   {
      SequenceCompiler.compile_main_sequence
      (
         this,
         fate_world.get_global_instructions()
      );
   }

   protected void add_registers ()
   throws Throwable
   {
      for (final DictType type: registers.get_context_structure_types())
      {
         wyrd_world.add_dict_type(type);
      }

      for (final Register register: registers.get_base_registers())
      {
         wyrd_world.add_register(register);
      }
   }

   public World world ()
   {
      return wyrd_world;
   }

   public RegisterManager registers ()
   {
      return registers;
   }

   public InstructionManager assembler ()
   {
      return assembler;
   }
}
