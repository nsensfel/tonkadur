package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.TextNode;

public class Sentence extends TextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String text;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Sentence
   (
      final Origin origin,
      final String text
   )
   {
      super(origin);

      this.text = text;
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_sentence(this);
   }

   public String get_text ()
   {
      return text;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Sentence ");
      sb.append(text);
      sb.append(")");

      return sb.toString();
   }
}
