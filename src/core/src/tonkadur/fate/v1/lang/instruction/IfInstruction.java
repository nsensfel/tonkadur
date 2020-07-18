package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class IfInstruction extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode condition;
   protected final InstructionNode if_true;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfInstruction
   (
      final Origin origin,
      final ValueNode condition,
      final InstructionNode if_true
   )
   {
      super(origin);

      this.condition = condition;
      this.if_true = if_true;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfInstruction build
   (
      final Origin origin,
      final ValueNode condition,
      final InstructionNode if_true
   )
   throws InvalidTypeException
   {
      if (condition.get_type().get_base_type().equals(Type.BOOLEAN))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               condition.get_origin(),
               condition.get_type(),
               Collections.singleton(Type.BOOLEAN)
            )
         );
      }

      return new IfInstruction(origin, condition, if_true);
   }

   /**** Accessors ************************************************************/
   public ValueNode get_condition ()
   {
      return condition;
   }

   public InstructionNode get_if_true ()
   {
      return if_true;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IfInstruction");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());
      sb.append(System.lineSeparator());
      sb.append(if_true.toString());

      sb.append(")");

      return sb.toString();
   }
}
