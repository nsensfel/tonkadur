package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.ArrayList;
import java.util.List;

import tonkadur.fate.v1.lang.computation.generic.Range;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

public class RangeCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return Range.class;
   }

   public RangeCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final Range source;
      final List<Instruction> while_body;
      final ComputationCompiler start_cc, end_cc, inc_cc;
      final Register result, iterator, accumulator;
      final Address new_element_addr;

      source = (Range) computation;

      while_body = new ArrayList<Instruction>();

      result = reserve(MapType.MAP_TO_INT);
      result_as_address = result.get_address();
      result_as_computation = result.get_value();

      start_cc = new ComputationCompiler(compiler);
      end_cc = new ComputationCompiler(compiler);
      inc_cc = new ComputationCompiler(compiler);

      source.get_start().get_visited_by(start_cc);
      start_cc.generate_address();

      source.get_end().get_visited_by(end_cc);
      end_cc.generate_address();

      source.get_increment().get_visited_by(inc_cc);
      inc_cc.generate_address();

      assimilate(start_cc);
      assimilate(end_cc);
      assimilate(inc_cc);

      iterator = reserve(Type.INT);
      accumulator = reserve(Type.INT);

      init_instructions.add
      (
         new SetValue(iterator.get_address(), Constant.ZERO)
      );
      init_instructions.add
      (
         new SetValue(accumulator.get_address(), start_cc.get_computation())
      );

      new_element_addr =
         new RelativeAddress
         (
            result_as_address,
            new Cast(iterator.get_value(), Type.STRING),
            Type.INT
         );

      while_body.add(new Initialize(new_element_addr));
      while_body.add
      (
         new SetValue(new_element_addr, accumulator.get_value())
      );
      while_body.add
      (
         new SetValue
         (
            accumulator.get_address(),
            Operation.plus(accumulator.get_value(), inc_cc.get_computation())
         )
      );
      while_body.add
      (
         new SetValue
         (
            iterator.get_address(),
            Operation.plus(iterator.get_value(), Constant.ONE)
         )
      );

      init_instructions.add
      (
         tonkadur.wyrd.v1.compiler.util.While.generate
         (
            compiler.registers(),
            compiler.assembler(),
            Operation.or
            (
               Operation.and
               (
                  Operation.greater_than
                  (
                     inc_cc.get_computation(),
                     Constant.ZERO
                  ),
                  Operation.less_equal_than
                  (
                     accumulator.get_value(),
                     end_cc.get_computation()
                  )
               ),
               Operation.and
               (
                  Operation.less_equal_than
                  (
                     inc_cc.get_computation(),
                     Constant.ZERO
                  ),
                  Operation.greater_equal_than
                  (
                     accumulator.get_value(),
                     end_cc.get_computation()
                  )
               )
            ),
            compiler.assembler().merge(while_body)
         )
      );
   }
}
