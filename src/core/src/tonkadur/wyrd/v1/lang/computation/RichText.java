package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class RichText extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> content;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RichText (final List<Computation> content)
   {
      super(Type.RICH_TEXT);

      this.content = content;
   }

   /**** Accessors ************************************************************/
   public List<Computation> get_content ()
   {
      return content;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_rich_text(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(RichText ");

      for (final Computation text: content)
      {
         if (text == null)
         {
            sb.append("<null?!>");
         }
         else
         {
            sb.append(text.toString());
         }
      }

      sb.append(")");

      return sb.toString();
   }
}
