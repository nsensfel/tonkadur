package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class RemoveElementAtComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:remove_at");
      aliases.add("list:removeat");
      aliases.add("list:removeAt");
      aliases.add("set:remove_at");
      aliases.add("set:removeat");
      aliases.add("set:removeAt");

      return aliases;
   }

   public static GenericComputation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation index;
      final Computation collection;

      if (call_parameters.size() != 2)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      index = call_parameters.get(0);
      collection = call_parameters.get(1);

      index.expect_non_string();
      collection.expect_non_string();

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      RecurrentChecks.assert_can_be_used_as(index, Type.INT);

      return new RemoveElementAtComputation(origin, index, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected RemoveElementAtComputation
   (
      final Origin origin,
      final Computation index,
      final Computation collection
   )
   {
      super(origin, collection.get_type());

      this.collection = collection;
      this.index = index;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_index ()
   {
      return index;
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

      sb.append("(RemoveElementAt");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("index:");
      sb.append(System.lineSeparator());
      sb.append(index.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("collection:");
      sb.append(System.lineSeparator());
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
