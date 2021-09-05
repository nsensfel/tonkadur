package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class AddElementComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:add");
      aliases.add("list:add_element");
      aliases.add("list:addelement");
      aliases.add("list:addElement");
      aliases.add("set:add");
      aliases.add("set:add_element");
      aliases.add("set:addelement");
      aliases.add("set:addElement");

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
      final Computation element;
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
                  + " <element: X>+ <collection: (LIST X)|(SET X)>)"
               )
            )
         );

         return null;
      }


      if (call_parameters.size() > 2)
      {
         final int param_size;
         Computation temp_collection;
         List<Computation> temp_params;

         param_size = call_parameters.size();

         temp_collection = call_parameters.get(param_size - 1);

         temp_params = new ArrayList<Computation>();
         temp_params.add(temp_collection);
         temp_params.add(temp_collection);

         for
         (
            final Computation addition:
               call_parameters.subList(0, (param_size - 2))
         )
         {
            temp_params.set(0, addition);

            temp_collection =
               build
               (
                  origin,
                  alias,
                  temp_params
               );
         }

         element = call_parameters.get(param_size - 2);
         collection = temp_collection;
      }
      else
      {
         element = call_parameters.get(0);
         collection = call_parameters.get(1);
      }

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.propagate_expected_types_and_assert_is_a_set_of
         (
            collection,
            element
         );
      }
      else
      {
         RecurrentChecks.propagate_expected_types_and_assert_is_a_list_of
         (
            collection,
            element
         );
      }

      return new AddElementComputation(origin, element, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation element;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementComputation
   (
      final Origin origin,
      final Computation element,
      final Computation collection
   )
   {
      super(origin, collection.get_type());

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

   public Computation get_element ()
   {
      return element;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AddElement");
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
