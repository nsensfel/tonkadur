package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;
import tonkadur.fate.v1.lang.instruction.InstructionList;

public class RemoveElementsOf extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:remove_all");
      aliases.add("list:removeall");
      aliases.add("list:removeAll");
      aliases.add("set:remove_all");
      aliases.add("set:removeall");
      aliases.add("set:removeAll");

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

      if (call_parameters.size() < 2)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + "! <X>+ <(LIST X)|(SET X) REFERENCE>)"
            )
         );

         return null;
      }
      else if (call_parameters.size() > 2)
      {
         final int size_minus_one;
         final List<Instruction> result;
         final List<Computation> sub_call_parameters;

         size_minus_one = call_parameters.size() - 1;

         result = new ArrayList<Instruction>();
         sub_call_parameters = new ArrayList<Computation>();

         sub_call_parameters.add(call_parameters.get(size_minus_one));
         sub_call_parameters.add(sub_call_parameters.get(0));

         for (int i = 0; i < size_minus_one; ++i)
         {
            final Computation added_element;

            added_element = call_parameters.get(i);

            sub_call_parameters.set(0, added_element);

            result.add(build(origin, alias, sub_call_parameters));
         }

         return new InstructionList(origin, result);
      }


      other_collection = call_parameters.get(0);
      collection = call_parameters.get(1);

      other_collection.expect_non_string();
      collection.expect_non_string();

      RecurrentChecks.assert_is_a_collection(other_collection);

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      RecurrentChecks.assert_can_be_used_as
      (
         other_collection.get_origin(),
         ((CollectionType) other_collection.get_type()).get_content_type(),
         ((CollectionType) collection.get_type()).get_content_type()
      );

      collection.use_as_reference();

      return new RemoveElementsOf(origin, other_collection, collection);
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
   protected RemoveElementsOf
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

      sb.append("(RemoveElementsOf");
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
