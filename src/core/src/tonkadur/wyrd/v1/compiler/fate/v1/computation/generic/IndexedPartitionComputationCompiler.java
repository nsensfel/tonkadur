package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.IndexedPartitionComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.type.DictType;
import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class IndexedPartitionComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return IndexedPartitionComputation.class;
   }

   public IndexedPartitionComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final IndexedPartitionComputation source;
      final ComputationCompiler lambda_cc, in_collection_cc;
      final Address car_addr, cdr_addr;
      final Register result;

      source = (IndexedPartitionComputation) computation;

      result = reserve(DictType.WILD);

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      assimilate(lambda_cc);

      in_collection_cc = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(in_collection_cc);

      if (in_collection_cc.has_init())
      {
         init_instructions.add(in_collection_cc.get_init());
      }

      car_addr =
         new RelativeAddress
         (
            result_as_address,
            Constant.string_value("0"),
            in_collection_cc.get_computation().get_type()
         );

      init_instructions.add
      (
         new SetValue
         (
            car_addr,
            in_collection_cc.get_computation()
         )
      );

      cdr_addr =
         new RelativeAddress
         (
            result_as_address,
            Constant.string_value("1"),
            in_collection_cc.get_computation().get_type()
         );

      in_collection_cc.release_registers(init_instructions);

      init_instructions.add(new Initialize(cdr_addr));

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.IndexedPartitionLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            car_addr,
            cdr_addr,
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
            ).is_set()
         )
      );
   }
}
