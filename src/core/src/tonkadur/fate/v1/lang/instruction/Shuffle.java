package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class Shuffle extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Shuffle
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
   public static Shuffle build
   (
      final Origin origin,
      final Reference collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_list(collection);

      return new Shuffle(origin, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_shuffle(this);
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

      sb.append("(Shuffle ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
