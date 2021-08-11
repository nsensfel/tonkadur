package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.AddressOperator;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class AddressOperatorCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return AddressOperator.class;
   }

   public AddressOperatorCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final AddressOperator source;
      final ComputationCompiler target_cc;

      source = (AddressOperator) computation;

      target_cc = new ComputationCompiler(compiler);

      source.get_target().get_visited_by(target_cc);

      assimilate(target_cc);

      result_as_computation = target_cc.get_address();
   }
}
