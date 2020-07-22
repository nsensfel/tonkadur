package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.TextNode;

public class Space extends TextNode
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Space (final Origin origin)
   {
      super(origin);
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_space(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(Space)";
   }
}
