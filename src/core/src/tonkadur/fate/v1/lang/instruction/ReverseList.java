package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class ReverseList extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ReverseList
   (
      final Origin origin,
      final Reference collection
   )
   {
      super(origin);

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ReverseList build
   (
      final Origin origin,
      final Reference collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_list(collection);

      return new ReverseList(origin, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_reverse_list(this);
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

      sb.append("(ReverseList ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
