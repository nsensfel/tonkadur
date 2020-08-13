package tonkadur.fate.v1.lang.instruction;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;

import tonkadur.fate.v1.lang.Variable;

public class LocalVariable extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Variable variable;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public LocalVariable (final Variable variable)
   {
      super(variable.get_origin());

      this.variable = variable;
   }


   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_local_variable(this);
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

      sb.append("(LocalVariable ");
      sb.append(variable.toString());

      sb.append(")");

      return sb.toString();
   }
}
