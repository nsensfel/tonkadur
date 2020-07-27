package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.RichTextNode;

public class Newline extends RichTextNode
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Newline (final Origin origin)
   {
      super(origin);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_newline(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(Newline)";
   }
}
