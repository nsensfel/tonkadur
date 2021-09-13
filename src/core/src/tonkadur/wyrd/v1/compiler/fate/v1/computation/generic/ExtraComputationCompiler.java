package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.ArrayList;
import java.util.List;

import tonkadur.fate.v1.lang.computation.generic.ExtraComputation;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class ExtraComputationCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return ExtraComputation.class;
   }

   public ExtraComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final ExtraComputation source;
      final List<Computation> parameters;

      source = (ExtraComputation) computation;

      parameters = new ArrayList<Computation>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation p: source.get_parameters()
      )
      {
         final ComputationCompiler cc;

         cc = new ComputationCompiler(compiler);

         p.get_visited_by(cc);

         assimilate(cc);

         parameters.add(cc.get_computation());
      }

      result_as_computation =
         new tonkadur.wyrd.v1.lang.computation.ExtraComputation
         (
            TypeCompiler.compile(compiler, source.get_type()),
            source.get_computation_name(),
            parameters
         );
   }
}
