package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Clear extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("clear");
      aliases.add("empty");

      aliases.add("list:clear");
      aliases.add("list:empty");

      aliases.add("set:clear");
      aliases.add("set:empty");

      return aliases;
   }

   public static GenericInstruction build
   (
      final Origin origin,
      final String alias_,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation collection;

      if (call_parameters.size() != 1)
      {
         // TODO: Error.
         System.err.print
         (
            "[E] Wrong number of arguments at "
            + origin.toString()
         );
      }

      collection = call_parameters.get(0);

      collection.expect_non_string();

      RecurrentChecks.assert_is_a_collection(collection);

      return new Clear(origin, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Clear
   (
      final Origin origin,
      final Computation collection
   )
   {
      super(origin);

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

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

      sb.append("(Clear ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
