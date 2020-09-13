package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class RemoveElementAt extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected RemoveElementAt
   (
      final Origin origin,
      final Computation index,
      final Reference collection
   )
   {
      super(origin);

      this.collection = collection;
      this.index = index;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static RemoveElementAt build
   (
      final Origin origin,
      final Computation index,
      final Reference collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection(collection);
      RecurrentChecks.assert_can_be_used_as(index, Type.INT);

      return new RemoveElementAt(origin, index, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_remove_element_at(this);
   }

   public Computation get_index ()
   {
      return index;
   }

   public Reference get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
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
