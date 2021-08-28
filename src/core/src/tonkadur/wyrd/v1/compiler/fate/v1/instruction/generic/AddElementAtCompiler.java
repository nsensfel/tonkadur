package tonkadur.wyrd.v1.compiler.fate.v1.instruction.generic;

import tonkadur.fate.v1.lang.instruction.generic.AddElementAt;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.IfElseComputation;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.Operation;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.instruction.GenericInstructionCompiler;

public class AddElementAtCompiler extends GenericInstructionCompiler
{
   public static Class get_target_class ()
   {
      return AddElementAt.class;
   }

   public AddElementAtCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.instruction.GenericInstruction instruction
   )
   throws Throwable
   {
      final AddElementAt source;
      final Address collection_as_address;
      final ComputationCompiler index_compiler, element_compiler;
      final ComputationCompiler collection_compiler;
      final Register index_holder;

      source = (AddElementAt) instruction;

      index_holder = compiler.registers().reserve(Type.INT, result);

      index_compiler = new ComputationCompiler(compiler);

      source.get_index().get_visited_by(index_compiler);

      if (index_compiler.has_init())
      {
         result.add(index_compiler.get_init());
      }

      element_compiler = new ComputationCompiler(compiler);

      source.get_element().get_visited_by(element_compiler);

      if (element_compiler.has_init())
      {
         result.add(element_compiler.get_init());
      }

      collection_compiler = new ComputationCompiler(compiler);

      source.get_collection().get_visited_by(collection_compiler);

      if (collection_compiler.has_init())
      {
         result.add(collection_compiler.get_init());
      }

      result.add
      (
         new SetValue
         (
            index_holder.get_address(),
            new IfElseComputation
            (
               Operation.greater_than
               (
                  index_compiler.get_computation(),
                  new Size(collection_compiler.get_address())
               ),
               new Size(collection_compiler.get_address()),
               new IfElseComputation
               (
                  Operation.less_than
                  (
                     index_compiler.get_computation(),
                     Constant.ZERO
                  ),
                  Constant.ZERO,
                  index_compiler.get_computation()
               )
            )
         )
      );

      result.add
      (
         tonkadur.wyrd.v1.compiler.util.InsertAt.generate
         (
            compiler.registers(),
            compiler.assembler(),
            index_holder.get_address(),
            element_compiler.get_computation(),
            new Size(collection_compiler.get_address()),
            collection_compiler.get_address()
         )
      );

      compiler.registers().release(index_holder, result);

      index_compiler.release_registers(result);
      element_compiler.release_registers(result);
      collection_compiler.release_registers(result);
   }
}
