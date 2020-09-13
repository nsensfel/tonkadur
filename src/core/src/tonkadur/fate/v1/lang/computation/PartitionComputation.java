package tonkadur.fate.v1.lang.computation;

import java.util.Collections;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class PartitionComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PartitionComputation
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection,
      final Type output_type
   )
   {
      super(origin, output_type);

      this.lambda_function = lambda_function;
      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static PartitionComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection
   )
   throws Throwable
   {
      final Type type;

      RecurrentChecks.assert_is_a_collection(collection);

      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         Type.BOOL,
         Collections.singletonList
         (
            ((CollectionType) collection.get_type()).get_content_type()
         )
      );

      type =
         new ConsType
         (
            origin,
            collection.get_type(),
            collection.get_type(),
            "auto generated"
         );

      return
         new PartitionComputation
         (
            origin,
            lambda_function,
            collection,
            type
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_partition(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
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

      sb.append("(Partition ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
