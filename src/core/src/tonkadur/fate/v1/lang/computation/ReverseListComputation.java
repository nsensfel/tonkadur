package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class ReverseListComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ReverseListComputation
   (
      final Origin origin,
      final Computation collection,
      final Type result_type
   )
   {
      super(origin, result_type);

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ReverseListComputation build
   (
      final Origin origin,
      final Computation collection
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection(collection);

      return
         new ReverseListComputation
         (
            origin,
            collection,
            CollectionType.build
            (
               origin,
               (((CollectionType) collection.get_type()).get_content_type()),
               false,
               "auto generated"
            )
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_reverse_list(this);
   }

   public Computation get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ReverseList ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
