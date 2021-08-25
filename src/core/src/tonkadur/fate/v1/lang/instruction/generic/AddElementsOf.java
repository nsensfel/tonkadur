package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class AddElementsOf extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

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

   public static Instruction build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation other_collection;
      final Computation collection;

      if (call_parameters.size() != 2)
      {
         // TODO: Error.
         System.err.print
         (
            "[E] Wrong number of arguments at "
            + origin.toString()
         );

         return null;
      }

      other_collection = call_parameters.get(0);
      collection = call_parameters.get(1);

      other_collection.expect_non_string();
      collection.expect_non_string();

      RecurrentChecks.assert_is_a_collection(collection);
      RecurrentChecks.assert_is_a_collection(other_collection);
      RecurrentChecks.assert_can_be_used_as
      (
         other_collection.get_origin(),
         ((CollectionType) other_collection.get_type()).get_content_type(),
         ((CollectionType) collection.get_type()).get_content_type()
      );

      collection.use_as_reference();

      return new AddElementsOf(origin, other_collection, collection);
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
   protected AddElementsOf
   (
      final Origin origin,
      final Computation other_collection,
      final Computation collection
   )
   {
      super(origin);

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
