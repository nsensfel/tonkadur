package tonkadur.wyrd.v1.compiler.fate.v1;

import tonkadur.error.Error;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.World;

public class ComputationCompiler extends FateVisitor
{
   protected final MacroManager macro_manager;
   protected final AnonymousVariableManager anonymous_variables;
   protected final World wyrd_world;
   protected final List<Instruction> pre_computation_instructions;
   protected final List<Ref> allocated_variables;
   protected Computation result;

   public ComputationCompiler
   (
      final MacroManager macro_manager,
      final AnonymousVariableManager anonymous_variables,
      final World wyrd_world
   )
   {
      this.macro_manager = macro_manager;
      this.anonymous_variables = anonymous_variables;
      this.wyrd_world = wyrd_world;

      allocated_variables = new ArrayList<Ref>();
      pre_computation_instructions = new ArrayList<Instruction>();
      result = null;
   }

   public List<Instruction> get_pre_instructions ()
   {
      return pre_computation_instructions;
   }

   public Computation get_computation ()
   {
      return result;
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
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_cast
   (
      final tonkadur.fate.v1.lang.computation.Cast n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_cond_value
   (
      final tonkadur.fate.v1.lang.computation.CondValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_constant
   (
      final tonkadur.fate.v1.lang.computation.Constant n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_count_operator
   (
      final tonkadur.fate.v1.lang.computation.CountOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_field_reference
   (
      final tonkadur.fate.v1.lang.computation.FieldReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_if_else_value
   (
      final tonkadur.fate.v1.lang.computation.IfElseValue n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_is_member_operator
   (
      final tonkadur.fate.v1.lang.computation.IsMemberOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_macro_value_call
   (
      final tonkadur.fate.v1.lang.computation.MacroValueCall n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_newline
   (
      final tonkadur.fate.v1.lang.computation.Newline n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_operation
   (
      final tonkadur.fate.v1.lang.computation.Operation n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_paragraph
   (
      final tonkadur.fate.v1.lang.computation.Paragraph n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_parameter_reference
   (
      final tonkadur.fate.v1.lang.computation.ParameterReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_ref_operator
   (
      final tonkadur.fate.v1.lang.computation.RefOperator n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_text_with_effect
   (
      final tonkadur.fate.v1.lang.computation.TextWithEffect n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_value_to_rich_text
   (
      final tonkadur.fate.v1.lang.computation.ValueToRichText n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }

   @Override
   public void visit_variable_reference
   (
      final tonkadur.fate.v1.lang.computation.VariableReference n
   )
   throws Throwable
   {
      throw new UnhandledASTElementException();
   }
}
