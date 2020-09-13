package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class PushElement extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation element;
   protected final Computation collection;
   protected final boolean is_from_left;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PushElement
   (
      final Origin origin,
      final Computation element,
      final Computation collection,
      final boolean is_from_left
   )
   {
      super(origin);

      this.collection = collection;
      this.element = element;
      this.is_from_left = is_from_left;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static PushElement build
   (
      final Origin origin,
      final Computation element,
      final Computation collection,
      final boolean is_from_left
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_list_of(collection, element);

      return new PushElement(origin, element, collection, is_from_left);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_push_element(this);
   }

   public Computation get_collection ()
   {
      return collection;
   }

   public Computation get_element ()
   {
      return element;
   }

   public boolean  is_from_left ()
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
         sb.append("(LeftPushElement");
      }
      else
      {
         sb.append("(RightPushElement");
      }

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
