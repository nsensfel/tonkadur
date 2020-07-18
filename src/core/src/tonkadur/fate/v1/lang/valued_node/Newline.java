package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.TextNode;

public class Newline extends TextNode
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Newline (final Origin origin)
   {
      super(origin);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(Newline)";
   }
}
