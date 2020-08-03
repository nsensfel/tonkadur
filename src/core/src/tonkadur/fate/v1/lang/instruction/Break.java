package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;

public class Break extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Break (final Origin origin)
   {
      super(origin);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_break(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(Break)";
   }
}
