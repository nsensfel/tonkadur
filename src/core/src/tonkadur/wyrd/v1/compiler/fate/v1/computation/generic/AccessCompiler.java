package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.Access;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class AccessCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return Access.class;
   }

   public AccessCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final Access source;
      final ComputationCompiler extra_address_cc, base_address_cc;

      source = (Access) computation;

      base_address_cc = new ComputationCompiler(compiler);
      extra_address_cc = new ComputationCompiler(compiler);

      source.get_parent().get_visited_by(base_address_cc);
      source.get_index().get_visited_by(extra_address_cc);

      base_address_cc.generate_address();

      assimilate(base_address_cc);
      assimilate(extra_address_cc);

      result_as_address =
         new RelativeAddress
         (
            base_address_cc.get_address(),
            new Cast(extra_address_cc.get_computation(), Type.STRING),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.CollectionType)
                     source.get_parent().get_type()
               ).get_content_type()
            )
         );
   }
}
