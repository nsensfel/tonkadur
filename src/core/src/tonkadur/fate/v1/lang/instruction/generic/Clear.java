package tonkadur.fate.v1.lang.instruction;

import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class Clear extends GenericInstruction
{
   protected static final Clear ARCHETYPE;

   static
   {
      final List<String> aliases;

      ARCHETYPE = new Clear(Origin.BASE_LANGUAGE, null);

      aliases = new ArrayList<String>();

      aliases.add("clear");
      aliases.add("empty");

      aliases.add("list:clear");
      aliases.add("list:empty");

      aliases.add("set:clear");
      aliases.add("set:empty");

      try
      {
         ARCHETYPE.register(aliases, null);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }
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
      super(origin, "clear");

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   @Override
   public GenericInstruction build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object _constructor_parameter
   )
   throws Throwable
   {
      final Computation collection;

      if (call_parameters.size() != 1)
      {
         // Error.
      }

      collection = call_parameters.get(0);

      collection.expect_non_string();

      RecurrentChecks.assert_is_a_collection(collection);

      return new Clear(origin, collection);
   }

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
