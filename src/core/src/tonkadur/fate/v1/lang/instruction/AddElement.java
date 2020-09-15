package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class AddElement extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation element;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElement
   (
      final Origin origin,
      final Computation element,
      final Reference collection
   )
   {
      super(origin);

      this.collection = collection;
      this.element = element;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AddElement build
   (
      final Origin origin,
      final Computation element,
      final Reference collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection_of(collection, element);

      return new AddElement(origin, element, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_add_element(this);
   }

   public Reference get_collection ()
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
