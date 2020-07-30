package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.instruction.SetValue;

import tonkadur.wyrd.v1.compiler.util.IfElse;

import tonkadur.wyrd.v1.lang.computation.*;


import tonkadur.wyrd.v1.lang.World;

public class ComputationCompiler
implements tonkadur.fate.v1.lang.meta.ComputationVisitor
{
   protected final Compiler compiler;
   protected final List<Instruction> init_instructions;
   protected final List<Ref> reserved_variables;
   protected boolean instructions_were_generated;
   protected Computation result_as_computation;
   protected Ref result_as_ref;

   public ComputationCompiler (final Compiler compiler)
   {
      this.compiler = compiler;

      reserved_variables = new ArrayList<Ref>();
      init_instructions = new ArrayList<Instruction>();
      result_as_ref = null;
      result_as_computation = null;
      instructions_were_generated = false;
   }

   public boolean has_init ()
   {
      return !init_instructions.isEmpty();
   }

   public Instruction get_init ()
   {
      instructions_were_generated = true;

      if (init_instructions.isEmpty())
      {
         return null;
      }

      return compiler.assembler().merge(init_instructions);
   }

   public Computation get_computation ()
   {
      if (result_as_computation != null)
      {
         return result_as_computation;
      }
      else
      {
         result_as_computation = new ValueOf(result_as_ref);

         return result_as_computation;
      }
   }

   public Ref get_ref ()
   {
      return result_as_ref;
   }

   public void generate_ref ()
   {
      if ((!instructions_were_generated) && (result_as_ref == null))
      {
         final Ref result;

         result = reserve(result_as_computation.get_type());

         init_instructions.add
         (
            new SetValue(result, result_as_computation)
         );


         result_as_ref = result;
      }
   }

   public void release_variables ()
   {
      for (final Ref ref: reserved_variables)
      {
         compiler.anonymous_variables().release(ref);
      }
   }

   protected void assimilate (final ComputationCompiler cc)
   {
      init_instructions.addAll(cc.init_instructions);
      reserved_variables.addAll(cc.reserved_variables);
   }

   protected Ref reserve (final Type t)
   {
      final Ref result;

      result = compiler.anonymous_variables().reserve(t);

      reserved_variables.add(result);

      return result;
   }

   @Override
   public void visit_at_reference
   (
      final tonkadur.fate.v1.lang.computation.AtReference n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_ref =
         new Ref
         (
            n_cc.get_computation(),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.RefType)
                     n.get_parent().get_type()
               ).get_referenced_type()
            )
         );
   }

   @Override
   public void visit_cast
   (
      final tonkadur.fate.v1.lang.computation.Cast n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_cond_value
   (
      final tonkadur.fate.v1.lang.computation.CondValue n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_constant
   (
      final tonkadur.fate.v1.lang.computation.Constant n
   )
   throws Throwable
   {
      result_as_computation =
         new Constant
         (
            TypeCompiler.compile(compiler, n.get_type()),
            n.get_value_as_string()
         );
   }

   @Override
   public void visit_count_operator
   (
      final tonkadur.fate.v1.lang.computation.CountOperator n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_field_reference
   (
      final tonkadur.fate.v1.lang.computation.FieldReference n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_parent().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_ref =
         new RelativeRef
         (
            n_cc.get_ref(),
            new Constant(Type.STRING, n.get_field_name()),
            TypeCompiler.compile
            (
               compiler,
               (
                  (tonkadur.fate.v1.lang.type.RefType)
                     n.get_parent().get_type()
               ).get_referenced_type()
            )
         );
   }

   @Override
   public void visit_if_else_value
   (
      final tonkadur.fate.v1.lang.computation.IfElseValue n
   )
   throws Throwable
   {
      final ComputationCompiler cond_cc, if_true_cc, if_false_cc;

      cond_cc = new ComputationCompiler(compiler);
      if_true_cc = new ComputationCompiler(compiler);
      if_false_cc = new ComputationCompiler(compiler);

      n.get_condition().get_visited_by(cond_cc);
      n.get_if_true().get_visited_by(if_true_cc);
      n.get_if_false().get_visited_by(if_false_cc);

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
          * equivalent and store the result in an anonymous variable to be used
          * here.
          */
         final Ref if_else_result;
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
            new SetValue(if_else_result, if_true_cc.get_computation())
         );

         if_false_branch.add
         (
            new SetValue(if_else_result, if_false_cc.get_computation())
         );

         if (cond_cc.has_init())
         {
            init_instructions.add(cond_cc.get_init());
         }

         init_instructions.add
         (
            IfElse.generate
            (
               compiler.anonymous_variables(),
               compiler.assembler(),
               cond_cc.get_computation(),
               compiler.assembler().merge(if_true_branch),
               compiler.assembler().merge(if_false_branch)
            )
         );

         reserved_variables.addAll(cond_cc.reserved_variables);
         reserved_variables.addAll(if_true_cc.reserved_variables);
         reserved_variables.addAll(if_false_cc.reserved_variables);

         result_as_computation = new ValueOf(if_else_result);
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

   @Override
   public void visit_is_member_operator
   (
      final tonkadur.fate.v1.lang.computation.IsMemberOperator n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_macro_value_call
   (
      final tonkadur.fate.v1.lang.computation.MacroValueCall n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_newline
   (
      final tonkadur.fate.v1.lang.computation.Newline n
   )
   throws Throwable
   {
      result_as_computation = new Newline();
   }

   @Override
   public void visit_operation
   (
      final tonkadur.fate.v1.lang.computation.Operation n
   )
   throws Throwable
   {
      /* TODO */
      result_as_computation = new Constant(Type.INT, "0");
   }

   @Override
   public void visit_paragraph
   (
      final tonkadur.fate.v1.lang.computation.Paragraph n
   )
   throws Throwable
   {
      final List<Computation> content;

      content = new ArrayList<Computation>();

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_content:
            n.get_content()
      )
      {
         final ComputationCompiler content_cc;

         content_cc = new ComputationCompiler(compiler);

         fate_content.get_visited_by(content_cc);

         assimilate(content_cc);

         content.add(content_cc.get_computation());
      }

      result_as_computation = new RichText(content);
   }

   @Override
   public void visit_parameter_reference
   (
      final tonkadur.fate.v1.lang.computation.ParameterReference n
   )
   throws Throwable
   {
      result_as_ref = compiler.macros().get_parameter_ref(n.get_name());
   }

   @Override
   public void visit_ref_operator
   (
      final tonkadur.fate.v1.lang.computation.RefOperator n
   )
   throws Throwable
   {
      final ComputationCompiler n_cc;

      n_cc = new ComputationCompiler(compiler);

      n.get_target().get_visited_by(n_cc);

      assimilate(n_cc);

      result_as_computation = n_cc.result_as_ref;
   }

   @Override
   public void visit_text_with_effect
   (
      final tonkadur.fate.v1.lang.computation.TextWithEffect n
   )
   throws Throwable
   {
      final ComputationCompiler text_cc;
      final List<Computation> parameters;

      text_cc = new ComputationCompiler(compiler);
      parameters = new ArrayList<Computation>();

      n.get_text().get_visited_by(text_cc);

      assimilate(text_cc);

      for
      (
         final tonkadur.fate.v1.lang.meta.Computation fate_param:
            n.get_parameters()
      )
      {
         final ComputationCompiler param_cc;

         param_cc = new ComputationCompiler(compiler);

         fate_param.get_visited_by(param_cc);

         assimilate(param_cc);

         parameters.add(param_cc.get_computation());
      }

      result_as_computation =
         new AddRichTextEffect
         (
            n.get_effect().get_name(),
            parameters,
            Collections.singletonList(text_cc.get_computation())
         );
   }

   @Override
   public void visit_value_to_rich_text
   (
      final tonkadur.fate.v1.lang.computation.ValueToRichText n
   )
   throws Throwable
   {
      n.get_value().get_visited_by(this);

      result_as_computation =
         new RichText(Collections.singletonList(result_as_computation));
   }

   @Override
   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.computation.VariableReference n
   )
   throws Throwable
   {
      result_as_ref =
         compiler.world().get_variable(n.get_variable().get_name()).get_ref();

      result_as_computation = new ValueOf(result_as_ref);
   }
}
