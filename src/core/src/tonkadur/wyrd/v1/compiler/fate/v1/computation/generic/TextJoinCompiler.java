package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.TextJoin;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.IfElseComputation;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Size;
import tonkadur.wyrd.v1.lang.computation.Text;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.TypeCompiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;


public class TextJoinCompiler extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return TextJoin.class;
   }

   public TextJoinCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final TextJoin source;
      final Register i, collection_size, accumulator;
      final List<Instruction> while_body;
      final List<Computation> next_chain;
      final ComputationCompiler link_cc, collection_cc;

      source = (TextJoin) computation;

      link_cc = new ComputationCompiler(compiler);
      collection_cc = new ComputationCompiler(compiler);

      source.get_link().get_visited_by(link_cc);
      source.get_text_collection().get_visited_by(collection_cc);

      assimilate(link_cc);
      assimilate(collection_cc);

      next_chain = new ArrayList<Computation>();
      while_body = new ArrayList<Instruction>();

      i = reserve(Type.INT);
      collection_size = reserve(Type.INT);
      accumulator = reserve(Type.TEXT);

      init_instructions.add
      (
         new SetValue(i.get_address(), Constant.ONE)
      );
      init_instructions.add
      (
         new SetValue
         (
            collection_size.get_address(),
            new Size(collection_cc.get_address())
         )
      );

      init_instructions.add
      (
         new SetValue
         (
            accumulator.get_address(),
            new IfElseComputation
            (
               Operation.equals(collection_size.get_value(), Constant.ZERO),
               new Text(new ArrayList<Computation>()),
               new ValueOf
               (
                  new RelativeAddress
                  (
                     collection_cc.get_address(),
                     new Cast(Constant.ZERO, Type.STRING),
                     Type.TEXT
                  )
               )
            )
         )
      );

      next_chain.add(accumulator.get_value());
      next_chain.add(link_cc.get_computation());
      next_chain.add
      (
         new ValueOf
         (
            new RelativeAddress
            (
               collection_cc.get_address(),
               new Cast(i.get_value(), Type.STRING),
               Type.TEXT
            )
         )
      );

      while_body.add
      (
         new SetValue(accumulator.get_address(), new Text(next_chain))
      );

      while_body.add
      (
         new SetValue
         (
            i.get_address(),
            Operation.plus(i.get_value(), Constant.ONE)
         )
      );

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.While.generate
         (
            compiler.registers(),
            compiler.assembler(),
            Operation.less_than(i.get_value(), collection_size.get_value()),
            compiler.assembler().merge(while_body)
         )
      );

      result_as_computation = accumulator.get_value();
   }
}
