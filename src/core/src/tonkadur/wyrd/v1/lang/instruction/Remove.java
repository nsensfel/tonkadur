package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.computation.Ref;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

public class Remove extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Ref reference;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Remove (final Computation index, final Ref reference)
   {
      this.index = index;
      this.reference = reference;
   }

   /**** Accessors ************************************************************/
   public Computation get_index ()
   {
      return index;
   }

   public Ref get_reference ()
   {
      return reference;
   }
}
