package tonkadur.wyrd.v1.compiler.fate.v1.computation;

import java.util.Map;
import java.util.HashMap;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

public abstract class GenericComputationCompiler extends ComputationCompiler
{
   protected static final Map<String, Class> ARCHETYPES;

   static
   {
      ARCHETYPES = new HashMap<String, Class>();
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

      registered_class = ARCHETYPES.get(computation.get_name());

      if (registered_class == null)
      {
         System.err.println
         (
            "[F] No Wyrd compilation process registered for generic Fate "
            + " computation \""
            + computation.get_name()
            + "\"."
         );

         System.exit(-1);
      }

      if (!GenericComputationCompiler.class.isAssignableFrom(registered_class))
      {
         System.err.println
         (
            "[F] The class registered to compile generic Fate "
            + " computation \""
            + computation.get_name()
            + "\" to Wyrd does not extend the GenericComputationCompiler class."
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

   protected void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
   }

   protected GenericComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }
}
