package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class AddElementsOfComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:add_all");
      aliases.add("list:addall");
      aliases.add("list:addAll");

      aliases.add("list:add_all_of");
      aliases.add("list:addallof");
      aliases.add("list:addAllOf");

      aliases.add("list:add_elements");
      aliases.add("list:addelements");
      aliases.add("list:addElements");

      aliases.add("list:add_elements_of");
      aliases.add("list:addelementsof");
      aliases.add("list:addElementsOf");

      aliases.add("list:add_all_elements");
      aliases.add("list:addallelements");
      aliases.add("list:addAllElements");

      aliases.add("list:add_all_elements_of");
      aliases.add("list:addallelementsof");
      aliases.add("list:addAllElementsOf");

      aliases.add("set:add_all");
      aliases.add("set:addall");
      aliases.add("set:addAll");

      aliases.add("set:add_all_of");
      aliases.add("set:addallof");
      aliases.add("set:addAllOf");

      aliases.add("set:add_elements");
      aliases.add("set:addelements");
      aliases.add("set:addElements");

      aliases.add("set:add_elements_of");
      aliases.add("set:addelementsof");
      aliases.add("set:addElementsOf");

      aliases.add("set:add_all_elements");
      aliases.add("set:addallelements");
      aliases.add("set:addAllElements");

      aliases.add("set:add_all_elements_of");
      aliases.add("set:addallelementsof");
      aliases.add("set:addAllElementsOf");

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
      final Computation other_collection;
      final Computation collection;

      if (call_parameters.size() < 2)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               (
                  "("
                  + alias
                  + " <added_collection: (LIST X)|(SET X)>+"
                  + " <receiving_collection: (LIST X)|(SET X)>)"
               )
            )
         );

         return null;
      }
      else if (call_parameters.size() > 2)
      {
         final int size_minus_one;
         Computation result;

         size_minus_one = call_parameters.size() - 1;

         result = call_parameters.get(size_minus_one);

         for (int i = 0; i < size_minus_one; ++i)
         {
            final List<Computation> temp_params;
            final Computation addition;

            addition = call_parameters.get(i);
            temp_params = new ArrayList<Computation>();

            temp_params.add(addition);
            temp_params.add(result);

            result = build(addition.get_origin(), alias, temp_params);
         }

         return result;
      }

      other_collection = call_parameters.get(0);
      collection = call_parameters.get(1);

      other_collection.expect_non_string();
      collection.expect_non_string();

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      RecurrentChecks.assert_is_a_collection(other_collection);
      RecurrentChecks.assert_can_be_used_as
      (
         other_collection.get_origin(),
         ((CollectionType) other_collection.get_type()).get_content_type(),
         ((CollectionType) collection.get_type()).get_content_type()
      );

      return new AddElementsOfComputation(origin, other_collection, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation other_collection;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementsOfComputation
   (
      final Origin origin,
      final Computation other_collection,
      final Computation collection
   )
   {
      super(origin, collection.get_type());

      this.collection = collection;
      this.other_collection = other_collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/

   public Computation get_source_collection ()
   {
      return other_collection;
   }

   public Computation get_target_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AddElementsOf");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("other_collection:");
      sb.append(System.lineSeparator());
      sb.append(other_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("collection:");
      sb.append(System.lineSeparator());
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }

}
