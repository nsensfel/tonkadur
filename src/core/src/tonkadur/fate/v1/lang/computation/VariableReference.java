package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

import tonkadur.fate.v1.lang.type.Type;

public class VariableReference extends Reference
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
      super(origin, reported_type, variable.get_name());
      this.variable = variable;
   }
   /**** Constructors *********************************************************/

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
      super(origin, variable.get_type(), variable.get_name());
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
