package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.optimizer.TextConstantOptimizer;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class Text extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> content;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Text (final List<Computation> content)
   {
      super(Type.TEXT);

      this.content = content;

      TextConstantOptimizer.optimize(content);
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
      cv.visit_text(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Text ");

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
