package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class AddElementsOfComputation extends Computation
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
   protected AddElementsOfComputation
   (
      final Origin origin,
      final Reference other_collection,
      final Reference collection
   )
   {
      super(origin, collection.get_type());

      this.collection = collection;
      this.other_collection = other_collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AddElementsOfComputation build
   (
      final Origin origin,
      final Reference other_collection,
      final Reference collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection(collection);
      RecurrentChecks.assert_is_a_collection(other_collection);
      RecurrentChecks.assert_can_be_used_as
      (
         other_collection.get_origin(),
         ((CollectionType) other_collection.get_type()).get_content_type(),
         ((CollectionType) collection.get_type()).get_content_type()
      );

      return new AddElementsOfComputation(origin, other_collection, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_add_elements_of(this);
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
