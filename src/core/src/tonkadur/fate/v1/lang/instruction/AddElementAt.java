package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class AddElementAt extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Computation element;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementAt
   (
      final Origin origin,
      final Computation index,
      final Computation element,
      final Reference collection
   )
   {
      super(origin);

      this.index = index;
      this.collection = collection;
      this.element = element;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AddElementAt build
   (
      final Origin origin,
      final Computation index,
      final Computation element,
      final Reference collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_list_of(collection, element);
      RecurrentChecks.assert_can_be_used_as(index, Type.INT);

      return new AddElementAt(origin, index, element, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_add_element_at(this);
   }

   public Reference get_collection ()
   {
      return collection;
   }

   public Computation get_index ()
   {
      return index;
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

      sb.append("(AddElementAt");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("index:");
      sb.append(System.lineSeparator());
      sb.append(index.toString());
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
