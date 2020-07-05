package tonkadur.fate.v1.lang.meta;

import java.util.Collection;
import java.util.HashSet;

import tonkadur.parser.Origin;

public abstract class InstructionNode extends Node
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Collection<InstructionNode> parents;
   protected InstructionNode child;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected InstructionNode (final Origin origin)
   {
      super(origin);

      parents = new HashSet<InstructionNode>();
      child = null;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public void link_parent (final InstructionNode parent)
   {
      parent.child = this;

      parents.add(parent);
   }

   public Collection<InstructionNode> get_parents ()
   {
      return parents;
   }

   public InstructionNode get_child ()
   {
      return child;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(Instruction)");

      return sb.toString();
   }
}
