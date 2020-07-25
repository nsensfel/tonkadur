package tonkadur.wyrd.v1.lang.instruction;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

public class Display extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> content;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Display (final List<Computation> content)
   {
      this.content = content;
   }

   /**** Accessors ************************************************************/
   public List<Computation> get_content ()
   {
      return content;
   }
}
