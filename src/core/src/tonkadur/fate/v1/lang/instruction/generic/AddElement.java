package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

import tonkadur.fate.v1.lang.instruction.InstructionList;

public class AddElement extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:add_element");
      aliases.add("list:addelement");
      aliases.add("list:addElement");
      aliases.add("list:add");

      aliases.add("set:add_element");
      aliases.add("set:addelement");
      aliases.add("set:addElement");
      aliases.add("set:add");

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
                  + "! <element: X>+ <collection: (LIST X)|(SET X)>)"
               )
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

      collection.use_as_reference();

      return new AddElement(origin, element, collection);
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
   protected AddElement
   (
      final Origin origin,
      final Computation element,
      final Computation collection
   )
   {
      super(origin);

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
