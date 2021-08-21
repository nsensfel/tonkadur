package tonkadur.wyrd.v1.compiler.fate.v1.computation.generic;

import java.util.List;
import java.util.ArrayList;

import tonkadur.fate.v1.lang.computation.generic.IfElseValue;

import tonkadur.wyrd.v1.compiler.fate.v1.Compiler;
import tonkadur.wyrd.v1.compiler.fate.v1.ComputationCompiler;

import tonkadur.wyrd.v1.compiler.fate.v1.computation.GenericComputationCompiler;

import tonkadur.wyrd.v1.lang.computation.IfElseComputation;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

public class IfElseValueCompiler
extends GenericComputationCompiler
{
   public static Class get_target_class ()
   {
      return IfElseValue.class;
   }

   public IfElseValueCompiler (final Compiler compiler)
   {
      super(compiler);
   }

   public void compile
   (
      final tonkadur.fate.v1.lang.computation.GenericComputation computation
   )
   throws Throwable
   {
      final IfElseValue source;
      final ComputationCompiler cond_cc, if_true_cc, if_false_cc;

      source = (IfElseValue) computation;

      cond_cc = new ComputationCompiler(compiler);
      if_true_cc = new ComputationCompiler(compiler);
      if_false_cc = new ComputationCompiler(compiler);

      source.get_condition().get_visited_by(cond_cc);
      source.get_if_true().get_visited_by(if_true_cc);
      source.get_if_false().get_visited_by(if_false_cc);

      if (if_true_cc.has_init() || if_false_cc.has_init())
      {
         /*
          * Unsafe ifelse computation: at least one of the branches needs to
          * use instructions with values *before* the condition has been
          * checked. This results in non-lazy evaluation, and is dangerous:
          * the condition might be a test to ensure that the computations of the
          * chosen branch are legal. In such cases, performing the potentially
          * illegal branch's instructions is likely to result in a runtime error
          * on the interpreter.
          *
          * Instead, we just convert the ifelse into an instruction-based
          * equivalent and store the result in an anonymous register to be used
          * here.
          */
         final Register if_else_result;
         final List<Instruction> if_true_branch;
         final List<Instruction> if_false_branch;

         if_else_result = reserve(if_true_cc.get_computation().get_type());

         if_true_branch = new ArrayList<Instruction>();
         if_false_branch = new ArrayList<Instruction>();

         if (if_true_cc.has_init())
         {
            if_true_branch.add(if_true_cc.get_init());
         }

         if (if_false_cc.has_init())
         {
            if_false_branch.add(if_false_cc.get_init());
         }

         if_true_branch.add
         (
            new SetValue
            (
               if_else_result.get_address(),
               if_true_cc.get_computation()
            )
         );

         if_false_branch.add
         (
            new SetValue
            (
               if_else_result.get_address(),
               if_false_cc.get_computation()
            )
         );

         if (cond_cc.has_init())
         {
            init_instructions.add(cond_cc.get_init());
         }

         init_instructions.add
         (
            tonkadur.wyrd.v1.compiler.util.IfElse.generate
            (
               compiler.registers(),
               compiler.assembler(),
               cond_cc.get_computation(),
               compiler.assembler().merge(if_true_branch),
               compiler.assembler().merge(if_false_branch)
            )
         );

         assimilate_reserved_registers(cond_cc);
         assimilate_reserved_registers(if_true_cc);
         assimilate_reserved_registers(if_false_cc);

         result_as_computation = if_else_result.get_value();
         result_as_address = if_else_result.get_address();
      }
      else
      {
         assimilate(cond_cc);
         assimilate(if_true_cc);
         assimilate(if_false_cc);

         result_as_computation =
            new IfElseComputation
            (
               cond_cc.get_computation(),
               if_true_cc.get_computation(),
               if_false_cc.get_computation()
            );
      }
   }
}
