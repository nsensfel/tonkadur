package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.RichTextNode;

public class Paragraph extends RichTextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<RichTextNode> content;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Paragraph
   (
      final Origin origin,
      final List<RichTextNode> content
   )
   {
      super(origin);

      this.content = content;
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_paragraph(this);
   }

   public List<RichTextNode> get_content ()
   {
      return content;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Paragraph ");

      for (final RichTextNode text: content)
      {
         sb.append(content.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
