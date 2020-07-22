package tonkadur.fate.v1.lang.valued_node;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class CountOperator extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode element;
   protected final ValueNode collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CountOperator
   (
      final Origin origin,
      final ValueNode element,
      final ValueNode collection
   )
   {
      super(origin, Type.INT);

      this.collection = collection;
      this.element = element;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static CountOperator build
   (
      final Origin origin,
      final ValueNode element,
      final ValueNode collection
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
         !Type.COLLECTION_TYPES.contains(collection_type.get_base_type())
         || !(collection_type instanceof CollectionType)
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

      collection_true_type = (CollectionType) collection_type;
      collection_element_type = collection_true_type.get_content_type();

      if
      (
         element.get_type().can_be_used_as(collection_element_type)
         ||
         (element.get_type().try_merging_with(collection_element_type) != null)
      )
      {
         return new CountOperator(origin, element, collection);
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

      return new CountOperator(origin, element, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_count_operator(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(CountOperator");
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
