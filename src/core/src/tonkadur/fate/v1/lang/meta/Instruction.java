package tonkadur.fate.v1.lang.meta;

import java.util.Collection;
import java.util.HashSet;

import tonkadur.parser.Origin;

public abstract class Instruction extends Node
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Collection<Instruction> parents;
   protected Instruction child;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Instruction (final Origin origin)
   {
      super(origin);

      parents = new HashSet<Instruction>();
      child = null;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public void link_parent (final Instruction parent)
   {
      parent.child = this;

      parents.add(parent);
   }

   public Collection<Instruction> get_parents ()
   {
      return parents;
   }

   public Instruction get_child ()
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
