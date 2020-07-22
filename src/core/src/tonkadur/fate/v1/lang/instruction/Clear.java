package tonkadur.fate.v1.lang.instruction;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class Clear extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Clear
   (
      final Origin origin,
      final ValueNode collection
   )
   {
      super(origin);

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Clear build
   (
      final Origin origin,
      final ValueNode collection
   )
   throws InvalidTypeException
   {
      if
      (
         !Type.COLLECTION_TYPES.contains(collection.get_type().get_base_type())
      )
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

      return new Clear(origin, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_clear(this);
   }

   public ValueNode get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Clear ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
