package tonkadur.fate.v1.lang.instruction;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class RemoveElementAt extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation index;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected RemoveElementAt
   (
      final Origin origin,
      final Computation index,
      final Reference collection
   )
   {
      super(origin);

      this.collection = collection;
      this.index = index;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static RemoveElementAt build
   (
      final Origin origin,
      final Computation index,
      final Reference collection
   )
   throws
      InvalidTypeException,
      IncomparableTypeException
   {
      final Type collection_type, hint;

      collection_type = collection.get_type();

      if (!(collection_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection.get_origin(),
               collection.get_type(),
               Type.COLLECTION_TYPES
            )
         );
      }

      if (index.get_type().can_be_used_as(Type.INT))
      {
         return new RemoveElementAt(origin, index, collection);
      }

      hint =
         (Type) index.get_type().generate_comparable_to(Type.INT);

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException
            (
               index.get_origin(),
               index.get_type(),
               Type.INT
            )
         );
      }

      return new RemoveElementAt(origin, index, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_remove_element_at(this);
   }

   public Computation get_index ()
   {
      return index;
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

      sb.append(origin.toString());
      sb.append("(RemoveElementAt");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("index:");
      sb.append(System.lineSeparator());
      sb.append(index.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("collection:");
      sb.append(System.lineSeparator());
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
