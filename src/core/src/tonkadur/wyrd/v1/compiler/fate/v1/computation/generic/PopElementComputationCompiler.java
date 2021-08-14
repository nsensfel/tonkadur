package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.PopElementComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.lang.type.DictType;
import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class PopElementComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return PopElementComputation.class;
   }

   public PopElementComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final PopElementComputation source;
      final ComputationCompiler address_compiler;
      final Register result;
      final Address car_addr, cdr_addr;

      source = (PopElementComputation) computation;

      result = reserve(DictType.WILD);
      result_as_computation = result.get_value();
      result_as_address = result.get_address();

      address_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(address_compiler);

      if (address_compiler.has_init())
      {
         init_instructions.add(address_compiler.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, address_compiler.get_computation())
      );

      car_addr =
         new RelativeAddress
         (
            result_as_address,
            new Constant(Type.STRING, "0"),
            address_compiler.get_computation().get_type()
         );

      init_instructions.add
      (
         new SetValue
         (
            car_addr,
            address_compiler.get_computation()
         )
      );

      address_compiler.release_registers(init_instructions);

      cdr_addr =
         new RelativeAddress
         (
            result_as_address,
            new Constant(Type.STRING, "1"),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.CollectionType)
                     source.get_collection().get_type()
               ).get_content_type()
            )
         );

      init_instructions.add(new Initialize(cdr_addr));

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.PopElement.generate
         (
            compiler.registers(),
            compiler.assembler(),
            car_addr,
            cdr_addr,
            source.is_from_left()
         )
      );
   }
}
