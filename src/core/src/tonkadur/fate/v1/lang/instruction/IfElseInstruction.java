package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class IfElseInstruction extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode condition;
   protected final InstructionNode if_true;
   protected final InstructionNode if_false;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfElseInstruction
   (
      final Origin origin,
      final ValueNode condition,
      final InstructionNode if_true,
      final InstructionNode if_false
   )
   {
      super(origin);

      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfElseInstruction build
   (
      final Origin origin,
      final ValueNode condition,
      final InstructionNode if_true,
      final InstructionNode if_false
   )
   throws InvalidTypeException
   {
      if (!condition.get_type().get_base_type().equals(Type.BOOLEAN))
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

      return new IfElseInstruction(origin, condition, if_true, if_false);
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

   public InstructionNode get_if_false ()
   {
      return if_false;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IfElseInstruction");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());
      sb.append(System.lineSeparator());
      sb.append("If true:");
      sb.append(System.lineSeparator());
      sb.append(if_true.toString());
      sb.append(System.lineSeparator());
      sb.append("If false:");
      sb.append(System.lineSeparator());
      sb.append(if_false.toString());

      sb.append(")");

      return sb.toString();
   }
}
