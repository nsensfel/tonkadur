package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.List;
import java.util.ArrayList;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.World;

import tonkadur.wyrd.v1.compiler.util.AnonymousVariableManager;

public class ComputationCompiler
implements tonkadur.fate.v1.lang.meta.ComputationVisitor
{
   protected final MacroManager macro_manager;
   protected final AnonymousVariableManager anonymous_variables;
   protected final World wyrd_world;
   protected final List<Instruction> pre_computation_instructions;
   protected final List<Ref> allocated_variables;
   protected final boolean expect_ref;
   protected Computation result_as_computation;
   protected Ref result_as_ref;

   public ComputationCompiler
   (
      final MacroManager macro_manager,
      final AnonymousVariableManager anonymous_variables,
      final World wyrd_world,
      final boolean expect_ref
   )
   {
      this.macro_manager = macro_manager;
      this.anonymous_variables = anonymous_variables;
      this.wyrd_world = wyrd_world;
      this.expect_ref = expect_ref;

      allocated_variables = new ArrayList<Ref>();
      pre_computation_instructions = new ArrayList<Instruction>();
      result_as_ref = null;
      result_as_computation = null;
   }

   public List<Instruction> get_pre_instructions ()
   {
      return pre_computation_instructions;
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

   protected void generate_ref ()
   {
      final Ref result;

      result = anonymous_variables.reserve(result_as_computation.get_type());

      allocated_variables.add(result);

      result_as_ref = result;
   }

   public void free_anonymous_variables ()
   {
      for (final Ref ref: allocated_variables)
      {
         anonymous_variables.release(ref);
      }
   }

   @Override
   public void visit_at_reference
   (
      final tonkadur.fate.v1.lang.computation.AtReference n
   )
   throws Throwable
   {
      /* TODO: implement */
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
      /* TODO: implement */
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
      /* TODO: implement */
   }

   @Override
   public void visit_if_else_value
   (
      final tonkadur.fate.v1.lang.computation.IfElseValue n
   )
   throws Throwable
   {
      /* TODO: implement */
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
      /* TODO: implement */
   }

   @Override
   public void visit_operation
   (
      final tonkadur.fate.v1.lang.computation.Operation n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_paragraph
   (
      final tonkadur.fate.v1.lang.computation.Paragraph n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_parameter_reference
   (
      final tonkadur.fate.v1.lang.computation.ParameterReference n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_ref_operator
   (
      final tonkadur.fate.v1.lang.computation.RefOperator n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_text_with_effect
   (
      final tonkadur.fate.v1.lang.computation.TextWithEffect n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_value_to_rich_text
   (
      final tonkadur.fate.v1.lang.computation.ValueToRichText n
   )
   throws Throwable
   {
      /* TODO: implement */
   }

   @Override
   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.computation.VariableReference n
   )
   throws Throwable
   {
      /* TODO: implement */
   }
}
