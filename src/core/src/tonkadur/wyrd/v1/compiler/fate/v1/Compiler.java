package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.World;

public class Compiler
{
   /* Utility Class */
   private Compiler () { }

   public static World compile (final tonkadur.fate.v1.lang.World fate_world)
   throws Error
   {
      final World wyrd_world;

      wyrd_world = new World();

      compile_extensions(fate_world, wyrd_world);
      compile_types(fate_world, wyrd_world);
      compile_variables(fate_world, wyrd_world);
      compile_sequences(fate_world, wyrd_world);
      compile_main_sequence(fate_world, wyrd_world);

      return wyrd_world;
   }

   protected static void compile_extensions
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final World wyrd_world
   )
   {
      for (final String extension: fate_world.get_required_extensions())
      {
         wyrd_world.add_required_extension(extension);
      }
   }

   protected static void compile_types
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final World wyrd_world
   )
   throws Error
   {
      for
      (
         final tonkadur.fate.v1.lang.type.Type type:
            fate_world.types().get_all()
      )
      {
         TypeCompiler.compile(type, wyrd_world);
      }
   }

   protected static void compile_variables
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final World wyrd_world
   )
   throws Error
   {
      for
      (
         final tonkadur.fate.v1.lang.Variable variable:
            fate_world.variables().get_all()
      )
      {
         VariableCompiler.compile(variable, wyrd_world);
      }
   }

   protected static void compile_sequences
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final World wyrd_world
   )
   throws Error
   {
      for
      (
         final tonkadur.fate.v1.lang.Sequence sequence:
            fate_world.sequences().get_all()
      )
      {
         SequenceCompiler.compile(sequence, wyrd_world);
      }
   }

   protected static void compile_main_sequence
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final World wyrd_world
   )
   throws Error
   {
      SequenceCompiler.compile_main_sequence
      (
         fate_world.get_global_instructions(),
         wyrd_world
      );
   }
}
