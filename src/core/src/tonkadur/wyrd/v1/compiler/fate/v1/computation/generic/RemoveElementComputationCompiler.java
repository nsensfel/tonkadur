package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.RemoveElementComputation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class RemoveElementComputationCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return RemoveElementComputation.class;
   }

   public RemoveElementComputationCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final RemoveElementComputation source;
      final ComputationCompiler elem_cc, collection_cc;
      final Register result;

      source = (RemoveElementComputation) computation;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      result = reserve(TypeCompiler.compile(compiler, source.get_type()));
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      source.get_collection().get_visited_by(collection_cc);

      if (collection_cc.has_init())
      {
         init_instructions.add(collection_cc.get_init());
      }

      init_instructions.add
      (
         new SetValue(result_as_address, collection_cc.get_computation())
      );

      collection_cc.release_registers(init_instructions);

      source.get_element().get_visited_by(elem_cc);

      elem_cc.generate_address();

      assimilate(elem_cc);

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.RemoveOneOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            elem_cc.get_computation(),
            result_as_address,
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
            ).is_set()
         )
      );
   }
}
