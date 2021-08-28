package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class PopElement extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:popleft");
      aliases.add("list:pop_left");
      aliases.add("list:popLeft");
      aliases.add("list:popleftelement");
      aliases.add("list:pop_left_element");
      aliases.add("list:popLeftElement");
      aliases.add("list:popright");
      aliases.add("list:pop_right");
      aliases.add("list:popRight");
      aliases.add("list:poprightelement");
      aliases.add("list:pop_right_element");
      aliases.add("list:popRightElement");
      aliases.add("set:popleft");
      aliases.add("set:pop_left");
      aliases.add("set:popLeft");
      aliases.add("set:popleftelement");
      aliases.add("set:pop_left_element");
      aliases.add("set:popLeftElement");
      aliases.add("set:popright");
      aliases.add("set:pop_right");
      aliases.add("set:popRight");
      aliases.add("set:poprightelement");
      aliases.add("set:pop_right_element");
      aliases.add("set:popRightElement");

      return aliases;
   }

   public static Instruction build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation storage;
      final Computation collection;
      final boolean is_from_left;

      if (call_parameters.size() != 2)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      collection = call_parameters.get(0);
      storage = call_parameters.get(1);

      collection.expect_non_string();
      storage.expect_non_string();

      is_from_left = alias.contains("eft");

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set_of(collection, storage.get_type());
      }
      else
      {
         RecurrentChecks.assert_is_a_list_of(collection, storage.get_type());
      }

      collection.use_as_reference();
      storage.use_as_reference();

      return new PopElement(origin, storage, collection, is_from_left);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation storage;
   protected final Computation collection;
   protected final boolean is_from_left;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PopElement
   (
      final Origin origin,
      final Computation storage,
      final Computation collection,
      final boolean is_from_left
   )
   {
      super(origin);

      this.storage = storage;
      this.collection = collection;
      this.is_from_left = is_from_left;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Accessors ************************************************************/
   public Computation get_collection ()
   {
      return collection;
   }

   public Computation get_storage ()
   {
      return storage;
   }

   public boolean is_from_left ()
   {
      return is_from_left;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (is_from_left)
      {
         sb.append("(PopLeftElement ");
      }
      else
      {
         sb.append("(PopRightElement ");
      }

      sb.append(collection.toString());

      sb.append(" ");
      sb.append(storage.toString());
      sb.append(")");

      return sb.toString();
   }
}
