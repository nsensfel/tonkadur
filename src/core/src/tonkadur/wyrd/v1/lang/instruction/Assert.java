package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

public class Assert extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Assert (final Computation condition)
   {
      this.condition = condition;
   }

   /**** Accessors ************************************************************/
   public Computation get_condition ()
   {
      return condition;
   }
}
