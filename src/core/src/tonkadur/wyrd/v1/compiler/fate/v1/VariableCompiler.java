package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.Variable;

import tonkadur.wyrd.v1.lang.World;

public class VariableCompiler
{
   /* Utility Class */
   private VariableCompiler () { }

   public static void compile
   (
      final Compiler compiler,
      final tonkadur.fate.v1.lang.Variable fate_variable
   )
   throws Error
   {
      compiler.world().add_variable
      (
         new Variable
         (
            fate_variable.get_name(),
            fate_variable.get_scope().toString(),
            TypeCompiler.compile(compiler, fate_variable.get_type())
         )
      );
   }
}
