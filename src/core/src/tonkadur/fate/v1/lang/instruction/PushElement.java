package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

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
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type hint;
      final Type collection_type;
      final CollectionType collection_true_type;
      final Type collection_element_type;

      collection_type = collection.get_type();

      if (!(collection_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection.get_origin(),
               collection.get_type(),
               Collections.singletonList(Type.LIST)
            )
         );
      }

      collection_true_type = (CollectionType) collection_type;

      if (collection_true_type.is_set())
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection.get_origin(),
               collection.get_type(),
               Collections.singletonList(Type.LIST)
            )
         );
      }

      collection_element_type = collection_true_type.get_content_type();

      if
      (
         element.get_type().can_be_used_as(collection_element_type)
         ||
         (element.get_type().try_merging_with(collection_element_type) != null)
      )
      {
         return new PushElement(origin, element, collection, is_from_left);
      }

      ErrorManager.handle
      (
         new ConflictingTypeException
         (
            element.get_origin(),
            element.get_type(),
            collection_element_type
         )
      );

      hint =
         (Type) element.get_type().generate_comparable_to
         (
            collection_element_type
         );

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException
            (
               element.get_origin(),
               element.get_type(),
               collection_element_type
            )
         );
      }

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
