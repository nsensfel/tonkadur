package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class ReverseListComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:reverse");
      aliases.add("set:reverse");

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
      final Computation collection;

      if (call_parameters.size() != 1)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      collection = call_parameters.get(0);

      collection.expect_non_string();

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      return
         new ReverseListComputation
         (
            origin,
            collection,
            CollectionType.build
            (
               origin,
               (((CollectionType) collection.get_type()).get_content_type()),
               false,
               "auto generated"
            )
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ReverseListComputation
   (
      final Origin origin,
      final Computation collection,
      final Type result_type
   )
   {
      super(origin, result_type);

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ReverseList ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
