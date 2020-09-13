package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class PopElement extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;
   protected final boolean is_from_left;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PopElement
   (
      final Origin origin,
      final Computation collection,
      final boolean is_from_left
   )
   {
      super(origin);

      this.collection = collection;
      this.is_from_left = is_from_left;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static PopElement build
   (
      final Origin origin,
      final Computation collection,
      final boolean is_from_left
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection(collection);

      return new PopElement(origin, collection, is_from_left);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_pop_element(this);
   }

   public Computation get_collection ()
   {
      return collection;
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

      sb.append(")");

      return sb.toString();
   }
}
