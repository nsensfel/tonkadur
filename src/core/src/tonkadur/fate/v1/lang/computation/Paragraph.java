package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.Type;

public class Paragraph extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> content;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Paragraph build
   (
      final Origin origin,
      final List<Computation> content
   )
   throws Throwable
   {
      for (int i = content.size(); i > 0; i--)
      {
         final Computation c;

         c = content.get(i);

         c.expect_string();

         if (!c.get_type().can_be_used_as(Type.TEXT))
         {
            content.set(i, ValueToText.build(c));
         }
      }

      return new Paragraph(origin, content);
   }

   protected Paragraph
   (
      final Origin origin,
      final List<Computation> content
   )
   {
      super(origin, Type.TEXT);

      this.content = content;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_paragraph(this);
   }

   public List<Computation> get_content ()
   {
      return content;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Paragraph ");

      for (final Computation text: content)
      {
         sb.append(content.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
