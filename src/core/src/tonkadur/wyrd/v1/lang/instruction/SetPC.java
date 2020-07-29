package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

public class SetPC extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation value;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetPC (final Computation value)
   {
      this.value = value;
   }

   /**** Accessors ************************************************************/
   public Computation get_value ()
   {
      return value;
   }
}