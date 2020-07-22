package tonkadur.fate.v1.lang.valued_node;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.TextNode;

public class Paragraph extends TextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<TextNode> content;

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
      final List<TextNode> content
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

   public List<TextNode> get_content ()
   {
      return content;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Paragraph ");

      for (final TextNode text: content)
      {
         sb.append(content.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
