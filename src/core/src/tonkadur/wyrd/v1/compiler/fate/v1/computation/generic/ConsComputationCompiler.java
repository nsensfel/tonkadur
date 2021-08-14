package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.ConsComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class ConsComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return ConsComputation.class;
   }

   public ConsComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final ConsComputation source;
      final ComputationCompiler car_compiler, cdr_compiler;
      final Register result;

      source = (ConsComputation) computation;

      result = reserve(DictType.WILD);
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      car_compiler = new ComputationCompiler(compiler);

      source.get_car().get_visited_by(car_compiler);

      if (car_compiler.has_init())
      {
         init_instructions.add(car_compiler.get_init());
      }

      cdr_compiler = new ComputationCompiler(compiler);

      source.get_cdr().get_visited_by(cdr_compiler);

      if (cdr_compiler.has_init())
      {
         init_instructions.add(cdr_compiler.get_init());
      }

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.CreateCons.generate
         (
            compiler.registers(),
            compiler.assembler(),
            result_as_address,
            car_compiler.get_computation(),
            cdr_compiler.get_computation()
         )
      );

      car_compiler.release_registers(init_instructions);
      cdr_compiler.release_registers(init_instructions);
   }
}
