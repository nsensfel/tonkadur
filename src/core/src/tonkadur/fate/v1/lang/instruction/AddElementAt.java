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

public class AddElementAt extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Computation element;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementAt
   (
      final Origin origin,
      final Computation index,
      final Computation element,
      final Computation collection
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
      final Computation collection
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

      if
      (
         (!(collection_type instanceof CollectionType))
         || (((CollectionType) collection_type).is_set())
      )
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

      if (!index.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               index.get_origin(),
               index.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      collection_true_type = (CollectionType) collection_type;
      collection_element_type = collection_true_type.get_content_type();

      if
      (
         element.get_type().can_be_used_as(collection_element_type)
         ||
         (element.get_type().try_merging_with(collection_element_type) != null)
      )
      {
         return new AddElementAt(origin, index, element, collection);
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

      return new AddElementAt(origin, index, element, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_add_element_at(this);
   }

   public Computation get_collection ()
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
