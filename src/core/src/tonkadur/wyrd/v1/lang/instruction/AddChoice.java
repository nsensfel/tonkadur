package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

public class AddChoice extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation label;
   protected final Instruction effect;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddChoice (final Computation label, final Instruction effect)
   {
      this.label = label;
      this.effect = effect;
   }

   /**** Accessors ************************************************************/
   public Computation get_label ()
   {
      return label;
   }

   public Instruction get_effect ()
   {
      return effect;
   }
}
