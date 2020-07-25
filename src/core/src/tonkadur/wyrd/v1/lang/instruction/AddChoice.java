package tonkadur.wyrd.v1.lang.instruction;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

public class AddChoice extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation label;
   protected final List<Instruction> effect;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddChoice (final Computation label, final List<Instruction> effect)
   {
      this.label = label;
      this.effect = effect;
   }

   /**** Accessors ************************************************************/
   public Computation get_label ()
   {
      return label;
   }

   public List<Instruction> get_effect ()
   {
      return effect;
   }
}
