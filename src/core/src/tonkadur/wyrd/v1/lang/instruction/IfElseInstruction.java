package tonkadur.wyrd.v1.lang.instruction;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.Computation;

public class IfElseInstruction extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final List<Instruction> if_true;
   protected final List<Instruction> if_false;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public IfElseInstruction
   (
      final Computation condition,
      final List<Instruction> if_true,
      final List<Instruction> if_false
   )
   {
      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /**** Accessors ************************************************************/
   public Computation get_condition ()
   {
      return condition;
   }

   public List<Instruction> get_if_true ()
   {
      return if_true;
   }

   public List<Instruction> get_if_false ()
   {
      return if_false;
   }
}
