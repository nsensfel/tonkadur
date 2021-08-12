package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class AddElementAtComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:add_element_at");
      aliases.add("list:addelementat");
      aliases.add("list:addElementAt");
      aliases.add("list:add_at");
      aliases.add("list:addat");
      aliases.add("list:addAt");

      return aliases;
   }

   public static GenericComputation build
   (
      final Origin origin,
      final String _alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation index;
      final Computation element;
      final Computation collection;

      if (call_parameters.size() != 3)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      index = call_parameters.get(0);
      element = call_parameters.get(1);
      collection = call_parameters.get(2);

      index.expect_non_string();

      RecurrentChecks.propagate_expected_types_and_assert_is_a_list_of
      (
         collection,
         element
      );

      RecurrentChecks.assert_can_be_used_as(index, Type.INT);

      return new AddElementAtComputation(origin, index, element, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Computation element;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementAtComputation
   (
      final Origin origin,
      final Computation index,
      final Computation element,
      final Computation collection
   )
   {
      super(origin, collection.get_type());

      this.index = index;
      this.collection = collection;
      this.element = element;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_collection ()
   {
      return collection;
   }

   public Computation get_index ()
   {
      return index;
   }

   public Computation get_element ()
   {
      return element;
   }


   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AddElementAt");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("index:");
      sb.append(System.lineSeparator());
      sb.append(index.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("element:");
      sb.append(System.lineSeparator());
      sb.append(element.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("collection:");
      sb.append(System.lineSeparator());
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
