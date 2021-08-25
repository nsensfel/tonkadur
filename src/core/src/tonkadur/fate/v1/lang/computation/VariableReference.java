package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.Type;

public class VariableReference extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Variable variable;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected VariableReference
   (
      final Origin origin,
      final Type reported_type,
      final Variable variable
   )
   {
      super(origin, reported_type);
      this.variable = variable;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public VariableReference
   (
      final Origin origin,
      final Variable variable
   )
   {
      super(origin, variable.get_type());
      this.variable = variable;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_variable_reference(this);
   }

   public Variable get_variable ()
   {
      return variable;
   }

   @Override
   public void use_as_reference ()
   throws ParsingError
   {
      // This allows its use as reference.
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(VariableReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(variable.get_name());
      sb.append(")");

      return sb.toString();
   }
}
