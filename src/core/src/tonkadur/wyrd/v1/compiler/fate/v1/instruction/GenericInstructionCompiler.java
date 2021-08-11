package tonkadur.wyrd.v1.compiler.fate.v1.instruction;

import java.util.Map;
import java.util.HashMap;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.InstructionCompiler;

public abstract class GenericInstructionCompiler extends InstructionCompiler
{
   protected static final Map<Class, Class> COMPILERS;

   static
   {
      COMPILERS = new HashMap<Class, Class>();
   }

   public static void register (final Class c)
   {
      try
      {
         COMPILERS.put
         (
            (Class) c.getDeclaredMethod("get_target_class").invoke
            (
               /* object  = */ null
            ),
            c
         );
      }
      catch (final Throwable t)
      {
         System.err.println
         (
            "[F] Could not get target class for Wyrd Generic Instruction"
            + " Compiler '"
            + c.getName()
            + "':"
         );

         t.printStackTrace();
      }
   }

   public static InstructionCompiler handle
   (
      final Compiler compiler,
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final Class registered_class;
      final GenericInstructionCompiler gic;

      registered_class = COMPILERS.get(instruction.getClass());

      if (registered_class == null)
      {
         System.err.println
         (
            "[F] No Wyrd compilation process registered for generic Fate"
            + " instruction \""
            + instruction.getClass().getName()
            + "\"."
         );

         System.err.println("Registered compilers:");

         for (final Class c: COMPILERS.keySet())
         {
            System.err.println
            (
               "- "
               + COMPILERS.get(c).getName()
               + " for "
               + c.getName()
               + "."
            );
         }

         System.exit(-1);
      }

      gic =
         (GenericInstructionCompiler)
         registered_class.getDeclaredConstructor(Compiler.class).newInstance
         (
            compiler
         );

      gic.compile(instruction);

      return gic;
   }

   protected abstract void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable;

   protected GenericInstructionCompiler (final Compiler compiler)
   {
      super(compiler);
   }
}
