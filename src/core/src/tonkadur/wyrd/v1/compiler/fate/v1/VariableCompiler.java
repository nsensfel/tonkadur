package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.Variable;

import tonkadur.wyrd.v1.lang.World;

public class VariableCompiler
{
   /* Utility Class */
   private VariableCompiler () { }

   public static Variable compile
   (
      tonkadur.fate.v1.lang.Variable fate_variable,
      final World wyrd_world
   )
   throws Error
   {
      Variable result;

      result = wyrd_world.get_variable(fate_variable.get_name());

      if (result != null)
      {
         return result;
      }

      result =
         new Variable
         (
            fate_variable.get_name(),
            fate_variable.get_scope().toString(),
            TypeCompiler.compile(fate_variable.get_type())
         );

      wyrd_world.add_variable(result);

      return result;
   }
}
