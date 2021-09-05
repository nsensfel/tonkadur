package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class TextJoin extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("join_text");
      aliases.add("jointext");
      aliases.add("joinText");
      aliases.add("text_join");
      aliases.add("textJoin");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation text_collection;
      final Computation link;

      if (call_parameters.size() != 2)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + " <(LIST TEXT)|(SET TEXT)> <link: TEXT>)"
            )
         );

         return null;
      }

      text_collection = call_parameters.get(0);
      link = call_parameters.get(1);

      text_collection.expect_non_string();
      link.expect_string();

      RecurrentChecks.propagate_expected_types_and_assert_is_a_collection_of
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

      return new TextJoin(origin, text_collection, link);
   }

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
      super(origin, Type.TEXT);

      this.text_collection = text_collection;
      this.link = link;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Accessors ************************************************************/
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
