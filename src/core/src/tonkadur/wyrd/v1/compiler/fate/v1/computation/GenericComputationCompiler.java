package tonkadur.wyrd.v1.compiler.fate.v1.computation;

import java.util.Map;
import java.util.HashMap;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

public abstract class GenericComputationCompiler extends ComputationCompiler
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
            "[F] Could not get target class for Wyrd Generic Computation"
            + " Compiler '"
            + c.getName()
            + "':"
         );

         t.printStackTrace();
      }
   }

   public static ComputationCompiler handle
   (
      final Compiler compiler,
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final Class registered_class;
      final GenericComputationCompiler gcc;

      registered_class = COMPILERS.get(computation.getClass());

      if (registered_class == null)
      {
         System.err.println
         (
            "[F] No Wyrd compilation process registered for generic Fate "
            + " computation \""
            + computation.getClass().getName()
            + "\"."
         );

         System.exit(-1);
      }

      gcc =
         (GenericComputationCompiler)
         registered_class.getDeclaredConstructor(Compiler.class).newInstance
         (
            compiler
         );

      gcc.compile(computation);

      return gcc;
   }

   protected abstract void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable;

   protected GenericComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }
}
