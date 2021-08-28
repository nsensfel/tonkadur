package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.RemoveAllOfElement;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class RemoveAllOfElementCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return RemoveAllOfElement.class;
   }

   public RemoveAllOfElementCompiler (final Compiler compiler)
   {
      super(compiler);
   }

      /*
       * Fate:
       * (remove_all_of element collection)
       *
       * Wyrd:
       * (declare_variable <element_type> .elem)
       * (declare_variable int .collection_size)
       *
       * (set .elem element)
       * (set .collection_size (size collection))
       *
       * <if collection is a list:
       *    <remove_all (var .elem) (var .collection_size) collection>
       * >
       * <if collection is a set:
       *    (declare_variable bool .found)
       *    (declare_variable int .index)
       *
       *    <binary_search
       *       (var .elem)
       *       (var .collection_size)
       *       collection
       *       .found
       *       .index
       *    >
       *    (ifelse (var .found)
       *       <remove_at (var .index) (var .collection_size) collection>
       *       (nop)
       *    )
       * >
       */
   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final RemoveAllOfElement source;
      final ComputationCompiler elem_cc, collection_cc;
      final Register collection_size;
      final Address elem, collection;

      source = (RemoveAllOfElement) instruction;

      elem_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.registers().reserve(Type.INT, result);

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

      collection = collection_cc.get_address();
      elem = elem_cc.get_address();

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      if
      (
         (
            (tonkadur.fate.v1.lang.type.CollectionType)
            source.get_collection().get_type()
         ).is_set()
      )
      {
         final Computation value_of_elem;
         final Register index, found;

         index = compiler.registers().reserve(Type.INT, result);
         found = compiler.registers().reserve(Type.BOOL, result);

         value_of_elem = new ValueOf(elem);

         result.add
         (
            tonkadur.wyrd.v1.compiler.util.BinarySearch.generate
            (
               compiler.registers(),
               compiler.assembler(),
               new ValueOf(elem),
               collection_size.get_value(),
               collection,
               found.get_address(),
               index.get_address()
            )
         );

         elem_cc.release_registers(result);

         result.add
         (
            tonkadur.wyrd.v1.compiler.util.If.generate
            (
               compiler.registers(),
               compiler.assembler(),
               found.get_value(),
               tonkadur.wyrd.v1.compiler.util.RemoveAt.generate
               (
                  compiler.registers(),
                  compiler.assembler(),
                  index.get_address(),
                  collection_size.get_value(),
                  collection
               )
            )
         );

         compiler.registers().release(index, result);
         compiler.registers().release(found, result);
      }
      else
      {
         result.add
         (
            tonkadur.wyrd.v1.compiler.util.RemoveAllOf.generate
            (
               compiler.registers(),
               compiler.assembler(),
               new ValueOf(elem),
               collection_size.get_value(),
               collection
            )
         );

         elem_cc.release_registers(result);
      }

      collection_cc.release_registers(result);

      compiler.registers().release(collection_size, result);
   }
}
