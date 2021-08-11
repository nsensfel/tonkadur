package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.AtReference;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;


public class AtReferenceCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return AtReference.class;
   }

   public AtReferenceCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final AtReference source;
      final ComputationCompiler parent_cc;

      source = (AtReference) computation;

      parent_cc = new ComputationCompiler(compiler);

      source.get_parent().get_visited_by(parent_cc);

      assimilate(parent_cc);

      result_as_address =
         new Address
         (
            parent_cc.get_computation(),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.PointerType)
                     source.get_parent().get_type()
               ).get_referenced_type()
            )
         );
   }
}
