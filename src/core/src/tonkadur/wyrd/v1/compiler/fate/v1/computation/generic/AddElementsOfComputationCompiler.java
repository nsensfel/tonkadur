package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.AddElementsOfComputation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class AddElementsOfComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return AddElementsOfComputation.class;
   }

   public AddElementsOfComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final AddElementsOfComputation source;
      final ComputationCompiler collection_in_cc, collection_cc;
      final Register result;

      source = (AddElementsOfComputation) computation;

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));

      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      collection_cc = new ComputationCompiler(compiler);

      source.get_target_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         init_instructions.add(collection_cc.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, collection_cc.get_computation())
      );

      collection_cc.release_registers(init_instructions);

      collection_in_cc = new ComputationCompiler(compiler);

      source.get_source_collection().get_visited_by(collection_in_cc);

      assimilate(collection_in_cc);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.AddElementsOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            collection_in_cc.get_address(),
            result_as_address,
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_target_collection().get_type()
            ).is_set()
         )
      );
   }
}
