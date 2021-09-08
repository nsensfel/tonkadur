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

public class Shuffle extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:shuffle");

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
      final Computation collection;

      if (call_parameters.size() < 1)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + "! <(LIST X) REFERENCE>+)"
            )
         );

         return null;
      }
      else if (call_parameters.size() > 1)
      {
         final int size_minus_one;
         final List<Instruction> result;
         final List<Computation> sub_call_parameters;

         size_minus_one = call_parameters.size();

         result = new ArrayList<Instruction>();
         sub_call_parameters = new ArrayList<Computation>();

         sub_call_parameters.add(call_parameters.get(0));

         for (int i = 0; i < size_minus_one; ++i)
         {
            final Computation added_element;

            added_element = call_parameters.get(i);

            sub_call_parameters.set(0, added_element);

            result.add(build(origin, alias, sub_call_parameters));
         }

         return new InstructionList(origin, result);
      }

      collection = call_parameters.get(0);

      collection.expect_non_string();

      RecurrentChecks.assert_is_a_list(collection);

      collection.use_as_reference();

      return new Shuffle(origin, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Shuffle
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

      sb.append("(Shuffle ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
