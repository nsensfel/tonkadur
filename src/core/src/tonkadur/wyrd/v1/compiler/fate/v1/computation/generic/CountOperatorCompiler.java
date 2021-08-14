package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import tonkadur.fate.v1.lang.computation.generic.CountOperator;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.IfElseComputation;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class CountOperatorCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return CountOperator.class;
   }

   public CountOperatorCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final CountOperator source;
      final ComputationCompiler collection_compiler, element_compiler;

      source = (CountOperator) computation;

      collection_compiler = new ComputationCompiler(compiler);
      element_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(collection_compiler);
      source.get_element().get_visited_by(element_compiler);

      collection_compiler.generate_address();

      assimilate(collection_compiler);
      assimilate(element_compiler);

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
         ).is_set()
      )
      {
         final Register was_found, index, element;

         was_found = reserve(Type.BOOL);
         index = reserve(Type.INT);
         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue
            (
               element.get_address(),
               element_compiler.get_computation()
            )
         );
         init_instructions.add
         (
            tonkadur.wyrd.v1.compiler.util.BinarySearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element.get_value(),
               new Size(collection_compiler.get_address()),
               collection_compiler.get_address(),
               was_found.get_address(),
               index.get_address()
            )
         );

         result_as_computation =
            new IfElseComputation
            (
               was_found.get_value(),
               Constant.ONE,
               Constant.ZERO
            );
      }
      else
      {
         final Register result, element;

         result = reserve(Type.INT);

         result_as_address = result.get_address();
         result_as_computation = result.get_value();

         element = reserve(element_compiler.get_computation().get_type());

         init_instructions.add
         (
            new SetValue
            (
               element.get_address(),
               element_compiler.get_computation()
            )
         );
         init_instructions.add
         (
            tonkadur.wyrd.v1.compiler.util.CountOccurrences.generate
            (
               compiler.registers(),
               compiler.assembler(),
               element.get_value(),
               new Size(collection_compiler.get_address()),
               collection_compiler.get_address(),
               result_as_address
            )
         );
      }
   }
}
