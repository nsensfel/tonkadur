package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class SubList extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:sublist");
      aliases.add("list:sub_list");
      aliases.add("list:subList");
      aliases.add("set:sublist");
      aliases.add("set:sub_list");
      aliases.add("set:subList");
      aliases.add("set:subset");
      aliases.add("set:sub_set");
      aliases.add("set:subSet");

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
      final Computation start;
      final Computation end;
      final Computation collection;

      if (call_parameters.size() != 3)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               (
                  "("
                  + alias
                  + "! <start_at: INT>"
                  + " <stop_before: INT>"
                  + " <(LIST X)|(SET X) REFERENCE>)"
               )
            )
         );

         return null;
      }

      start = call_parameters.get(0);
      end = call_parameters.get(1);
      collection = call_parameters.get(2);

      start.expect_non_string();
      end.expect_non_string();
      collection.expect_non_string();

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      RecurrentChecks.assert_can_be_used_as(start, Type.INT);
      RecurrentChecks.assert_can_be_used_as(end, Type.INT);

      collection.use_as_reference();

      return new SubList(origin, start, end, collection);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation start;
   protected final Computation end;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SubList
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Computation collection
   )
   {
      super(origin);

      this.start = start;
      this.end = end;
      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_start_index ()
   {
      return start;
   }

   public Computation get_end_index ()
   {
      return end;
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

      sb.append("(SubList ");
      sb.append(start.toString());
      sb.append(" ");
      sb.append(end.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
