package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class ResolveChoices extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ResolveChoices ()
   {
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_resolve_choices(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(ResolveChoices)";
   }
}
