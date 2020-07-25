package tonkadur.wyrd.v1.lang.instruction;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

public class While extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final List<Instruction> body;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public While
   (
      final Computation condition,
      final List<Instruction> body
   )
   {
      this.condition = condition;
      this.body = body;
   }

   /**** Accessors ************************************************************/
   public Computation get_condition ()
   {
      return condition;
   }

   public List<Instruction> get_body ()
   {
      return body;
   }
}
