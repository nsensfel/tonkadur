package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class SubList extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation start;
   protected final Computation end;
   protected final Reference collection_in;
   protected final Reference collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SubList
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Reference collection_in,
      final Reference collection_out
   )
   {
      super(origin);

      this.start = start;
      this.end = end;
      this.collection_in = collection_in;
      this.collection_out = collection_out;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static SubList build
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Reference collection_in,
      final Reference collection_out
   )
   throws Throwable
   {
      RecurrentChecks.assert_is_a_collection(collection_in);
      RecurrentChecks.assert_is_a_collection(collection_out);
      RecurrentChecks.assert_can_be_used_as
      (
         collection_in,
         collection_out.get_type()
      );
      RecurrentChecks.assert_can_be_used_as(start, Type.INT);
      RecurrentChecks.assert_can_be_used_as(end, Type.INT);

      return new SubList(origin, start, end, collection_in, collection_out);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_sublist(this);
   }

   public Computation get_start_index ()
   {
      return start;
   }

   public Computation get_end_index ()
   {
      return end;
   }

   public Reference get_collection_in ()
   {
      return collection_in;
   }

   public Reference get_collection_out ()
   {
      return collection_out;
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
      sb.append(collection_in.toString());
      sb.append(" ");
      sb.append(collection_out.toString());
      sb.append(")");

      return sb.toString();
   }
}
