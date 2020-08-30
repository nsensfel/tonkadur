package tonkadur.fate.v1.lang.instruction;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Reference;

public class AddElementsOf extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference other_collection;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementsOf
   (
      final Origin origin,
      final Reference other_collection,
      final Reference collection
   )
   {
      super(origin);

      this.collection = collection;
      this.other_collection = other_collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AddElementsOf build
   (
      final Origin origin,
      final Reference other_collection,
      final Reference collection
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type hint;
      final Type collection_type, other_collection_type;

      collection_type = collection.get_type();
      other_collection_type = other_collection.get_type();

      if (!(collection_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection.get_origin(),
               collection_type,
               Type.COLLECTION_TYPES
            )
         );
      }

      if (!(other_collection_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               other_collection.get_origin(),
               other_collection_type,
               Type.COLLECTION_TYPES
            )
         );
      }

      if (other_collection_type.can_be_used_as(collection_type))
      {
         return new AddElementsOf(origin, other_collection, collection);
      }

      ErrorManager.handle
      (
         new ConflictingTypeException
         (
            other_collection.get_origin(),
            other_collection_type,
            collection_type
         )
      );

      hint =
         (Type) other_collection.get_type().generate_comparable_to
         (
            collection_type
         );

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException
            (
               other_collection.get_origin(),
               other_collection_type,
               collection_type
            )
         );
      }

      return new AddElementsOf(origin, other_collection, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_add_elements_of(this);
   }

   public Reference get_source_collection ()
   {
      return other_collection;
   }

   public Reference get_target_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(AddElementsOf");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("other_collection:");
      sb.append(System.lineSeparator());
      sb.append(other_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("collection:");
      sb.append(System.lineSeparator());
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
