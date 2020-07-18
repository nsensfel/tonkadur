package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.functional.Cons;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class CondInstruction extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Cons<ValueNode, InstructionNode>> branches;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CondInstruction
   (
      final Origin origin,
      final List<Cons<ValueNode, InstructionNode>> branches
   )
   {
      super(origin);

      this.branches = branches;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static CondInstruction build
   (
      final Origin origin,
      final List<Cons<ValueNode, InstructionNode>> branches
   )
   throws InvalidTypeException
   {
      for (final Cons<ValueNode, InstructionNode> branch: branches)
      {
         if (branch.get_car().get_type().get_base_type().equals(Type.BOOLEAN))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  branch.get_car().get_origin(),
                  branch.get_car().get_type(),
                  Collections.singleton(Type.BOOLEAN)
               )
            );
         }
      }

      return new CondInstruction(origin, branches);
   }

   /**** Accessors ************************************************************/
   public List<Cons<ValueNode, InstructionNode>> get_branches ()
   {
      return branches;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(CondInstruction");
      sb.append(System.lineSeparator());

      for (final Cons<ValueNode, InstructionNode> branch: branches)
      {
         sb.append(System.lineSeparator());
         sb.append("if:");
         sb.append(branch.get_car().toString());

         sb.append(System.lineSeparator());
         sb.append("then:");
         sb.append(branch.get_cdr().toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
