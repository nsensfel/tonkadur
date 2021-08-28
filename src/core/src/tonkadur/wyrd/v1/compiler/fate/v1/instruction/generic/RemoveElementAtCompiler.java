package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.RemoveElementAt;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class RemoveElementAtCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return RemoveElementAt.class;
   }

   public RemoveElementAtCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final RemoveElementAt source;
      final ComputationCompiler index_cc, collection_cc;
      final Address collection;
      final Register collection_size;

      source = (RemoveElementAt) instruction;

      index_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      collection_size = compiler.registers().reserve(Type.INT, result);

      source.get_index().get_visited_by(index_cc);
      source.get_collection().get_visited_by(collection_cc);

      index_cc.generate_address();

      if (index_cc.has_init())
      {
         result.add(index_cc.get_init());
      }

      if (collection_cc.has_init())
      {
         result.add(collection_cc.get_init());
      }

      collection = collection_cc.get_address();

      result.add
      (
         new SetValue(collection_size.get_address(), new Size(collection))
      );

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.RemoveAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index_cc.get_address(),
            collection_size.get_value(),
            collection
         )
      );

      compiler.registers().release(collection_size, result);

      index_cc.release_registers(result);
      collection_cc.release_registers(result);
   }
}
