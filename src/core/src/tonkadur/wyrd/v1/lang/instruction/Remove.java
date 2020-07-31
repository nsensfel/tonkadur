package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.computation.Ref;

import tonkadur.wyrd.v1.lang.meta.Instruction;

public class Remove extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Ref reference;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Remove (final Ref reference)
   {
      this.reference = reference;
   }

   /**** Accessors ************************************************************/
   public Ref get_reference ()
   {
      return reference;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Remove ");
      sb.append(reference.toString());
      sb.append(")");

      return sb.toString();
   }
}
