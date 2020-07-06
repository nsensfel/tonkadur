package tonkadur.fate.v1.lang;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ValueNode;

public class VariableReference extends ValueNode
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
      super(origin, variable.get_type());
      this.variable = variable;
   }

   /**** Accessors ************************************************************/
   public Variable get_variable ()
   {
      return variable;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(VariableReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(variable.get_name());
      sb.append(")");

      return sb.toString();
   }
}
