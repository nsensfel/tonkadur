package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.RemoveElement;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class RemoveElementCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return RemoveElement.class;
   }

   public RemoveElementCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   /*
    * Fate:
    * (remove_element element collection)
    *
    * Wyrd:
    * (declare_variable <element_type> .elem)
    * (declare_variable int .collection_size)
    * (declare_variable boolean .found)
    * (declare_variable int .index)
    *
    * (set .elem element)
    * (set .collection_size (size collection))
    *
    * <if collection is a set:
    *    <BinarySearch
    *       (var .elem)
    *       (var .collection_size)
    *       collection
    *       .found
    *       .index
    *    >
    * >
    * <if collection is a list:
    *    <IterativeSearch
    *       (var .elem)
    *       (var .collection_size)
    *       collection
    *       .found
    *       .index
    *    >
    * >
    *
    * (if (var .found)
    *    <remove_at (var index) (var .collection_size) collection>
    *    (nop)
    * )
    */
   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final RemoveElement source;
      final ComputationCompiler elem_cc, collection_cc;

      source = (RemoveElement) instruction;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      source.get_element().get_visited_by(elem_cc);
      source.get_collection().get_visited_by(collection_cc);

      elem_cc.generate_address();

      if (elem_cc.has_init())
      {
         result.add(elem_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.RemoveOneOf.generate
         (
            compiler.registers(),
            compiler.assembler(),
            elem_cc.get_computation(),
            collection_cc.get_address(),
            (
               (tonkadur.fate.v1.lang.type.CollectionType)
               source.get_collection().get_type()
            ).is_set()
         )
      );

      elem_cc.release_registers(result);
      collection_cc.release_registers(result);
   }
}
