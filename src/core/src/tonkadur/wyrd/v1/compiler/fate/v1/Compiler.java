package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.wyrd.v1.compiler.util.AnonymousVariableManager;
import tonkadur.wyrd.v1.compiler.util.InstructionManager;

import tonkadur.wyrd.v1.lang.World;

public class Compiler
{
   protected final AnonymousVariableManager anonymous_variables;
   protected final MacroManager macro_manager;
   protected final InstructionManager assembler;
   protected final World wyrd_world;


   public static World compile (final tonkadur.fate.v1.lang.World fate_world)
   throws Throwable
   {
      final Compiler compiler;

      compiler = new Compiler();

      compiler.compile_extensions(fate_world);
      compiler.compile_types(fate_world);
      compiler.compile_variables(fate_world);

      compiler.compile_main_sequence(fate_world);

      compiler.compile_sequences(fate_world);

      return compiler.wyrd_world;
   }

   protected Compiler ()
   {
      macro_manager = new MacroManager();
      anonymous_variables = new AnonymousVariableManager();
      assembler = new InstructionManager();
      wyrd_world = new World();
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
      for
      (
         final tonkadur.fate.v1.lang.Variable variable:
            fate_world.variables().get_all()
      )
      {
         VariableCompiler.compile(this, variable);
      }
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

   public World world ()
   {
      return wyrd_world;
   }

   public AnonymousVariableManager anonymous_variables ()
   {
      return anonymous_variables;
   }

   public InstructionManager assembler ()
   {
      return assembler;
   }

   public MacroManager macros ()
   {
      return macro_manager;
   }
}
