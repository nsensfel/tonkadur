package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class RemoveAllOfElementComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:remove_each");
      aliases.add("list:removeeach");
      aliases.add("list:removeEach");
      aliases.add("list:remove_every");
      aliases.add("list:removeevery");
      aliases.add("list:removeEvery");
      aliases.add("set:remove_each");
      aliases.add("set:removeeach");
      aliases.add("set:removeEach");
      aliases.add("set:remove_every");
      aliases.add("set:removeevery");
      aliases.add("set:removeEvery");

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
               "(" + alias + " <element: X>+ <(LIST X)|(SET X)>)"
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

      element = call_parameters.get(0);
      collection = call_parameters.get(1);

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

      return new RemoveAllOfElementComputation(origin, element, collection);
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
   protected RemoveAllOfElementComputation
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
   public Computation get_element ()
   {
      return element;
   }

   public Computation get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(RemoveAllOfElement");
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
