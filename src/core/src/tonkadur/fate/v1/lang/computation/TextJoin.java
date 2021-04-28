package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.TextNode;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class TextJoin extends TextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation text_collection;
   protected final Computation link;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected TextJoin
   (
      final Origin origin,
      final Computation text_collection,
      final Computation link
   )
   {
      super(origin);

      this.text_collection = text_collection;
      this.link = link;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static TextJoin build
   (
      final Origin origin,
      final Computation text_collection,
      final Computation link
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection_of
      (
         origin,
         text_collection.get_type(),
         origin,
         Type.TEXT
      );

      RecurrentChecks.assert_can_be_used_as
      (
         link,
         Type.TEXT
      );

      return new TextJoin (origin, text_collection, link);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_text_join(this);
   }

   public Computation get_text_collection ()
   {
      return text_collection;
   }

   public Computation get_link ()
   {
      return link;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(TextJoin ");
      sb.append(link.toString());
      sb.append(" ");
      sb.append(text_collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
