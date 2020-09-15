package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class PopElement extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation storage_ptr;
   protected final Reference collection;
   protected final boolean is_from_left;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PopElement
   (
      final Origin origin,
      final Computation storage_ptr,
      final Reference collection,
      final boolean is_from_left
   )
   {
      super(origin);

      this.storage_ptr = storage_ptr;
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
      final Computation storage_ptr,
      final Reference collection,
      final boolean is_from_left
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection(collection);
      RecurrentChecks.assert_can_be_used_as
      (
         storage_ptr,
         new PointerType
         (
            origin,
            ((CollectionType) collection.get_type()).get_content_type(),
            "auto generated"
         )
      );

      return new PopElement(origin, storage_ptr, collection, is_from_left);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_pop_element(this);
   }

   public Reference get_collection ()
   {
      return collection;
   }

   public Computation get_storage_pointer ()
   {
      return storage_ptr;
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

      sb.append(" ");
      sb.append(storage_ptr.toString());
      sb.append(")");

      return sb.toString();
   }
}
