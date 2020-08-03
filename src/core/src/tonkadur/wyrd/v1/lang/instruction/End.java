package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Instruction;

public class End extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public End ()
   {
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(End)";
   }
}
