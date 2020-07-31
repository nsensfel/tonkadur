package tonkadur.fate.v1.lang.meta;

import java.util.Collection;
import java.util.HashSet;

import tonkadur.parser.Origin;

public abstract class Instruction extends Node
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Instruction (final Origin origin)
   {
      super(origin);
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      System.err.println("Unable to visit: " + toString());
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
