package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

public class SetValue extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation reference;
   protected final Computation value;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetValue (final Computation reference, final Computation value)
   {
      this.reference = reference;
      this.value = value;
   }

   /**** Accessors ************************************************************/
   public Computation get_reference ()
   {
      return reference;
   }

   public Computation get_value ()
   {
      return value;
   }
}
