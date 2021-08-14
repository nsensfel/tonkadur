package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.CarCdr;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class CarCdrCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return CarCdr.class;
   }

   public CarCdrCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final CarCdr source;
      final ComputationCompiler address_cc;

      source = (CarCdr) computation;

      address_cc = new ComputationCompiler(compiler);

      source.get_parent().get_visited_by(address_cc);

      assimilate(address_cc);

      result_as_address =
         new RelativeAddress
         (
            address_cc.get_address(),
            new Constant
            (
               Type.STRING,
               (source.is_car()? "0" : "1")
            ),
            TypeCompiler.compile(compiler, source.get_type())
         );
   }
}
