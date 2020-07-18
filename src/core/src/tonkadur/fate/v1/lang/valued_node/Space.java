package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

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

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(Space)";
   }
}
