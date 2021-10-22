package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.instruction.generic.IndexedPartition;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class IndexedPartitionCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return IndexedPartition.class;
   }

   public IndexedPartitionCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final IndexedPartition source;
      final ComputationCompiler lambda_cc, collection_in_cc, collection_out_cc;

      source = (IndexedPartition) instruction;

      lambda_cc = new ComputationCompiler(compiler);

      source.get_lambda_function().get_visited_by(lambda_cc);

      lambda_cc.generate_address();

      if (lambda_cc.has_init())
      {
         result.add(lambda_cc.get_init());
      }

      collection_in_cc = new ComputationCompiler(compiler);

      source.get_collection_in().get_visited_by(collection_in_cc);

      if (collection_in_cc.has_init())
      {
         result.add(collection_in_cc.get_init());
      }

      collection_out_cc = new ComputationCompiler(compiler);

      source.get_collection_out().get_visited_by(collection_out_cc);

      if (collection_out_cc.has_init())
      {
         result.add(collection_out_cc.get_init());
      }

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.IndexedPartitionLambda.generate
         (
            compiler.registers(),
            compiler.assembler(),
            lambda_cc.get_address(),
            collection_in_cc.get_address(),
            collection_out_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection_out().get_type()
            ).is_set()
         )
      );

      lambda_cc.release_registers(result);
      collection_in_cc.release_registers(result);
      collection_out_cc.release_registers(result);
   }
}
