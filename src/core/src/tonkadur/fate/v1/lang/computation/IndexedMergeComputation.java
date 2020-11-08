package tonkadur.fate.v1.lang.computation;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class IndexedMergeComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Computation collection_in_a;
   protected final Computation collection_in_b;
   protected final boolean to_set;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedMergeComputation
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in_a,
      final Computation collection_in_b,
      final boolean to_set,
      final List<Computation> extra_params,
      final Type output_type
   )
   {
      super(origin, output_type);

      this.lambda_function = lambda_function;
      this.collection_in_a = collection_in_a;
      this.collection_in_b = collection_in_b;
      this.to_set = to_set;
      this.extra_params = extra_params;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IndexedMergeComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in_a,
      final Computation collection_in_b,
      final boolean to_set,
      final List<Computation> extra_params
   )
   throws Throwable
   {
      final List<Type> types_in;

      types_in = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection_in_a);
      RecurrentChecks.assert_is_a_collection(collection_in_b);

      types_in.add(Type.INT);

      types_in.add
      (
         ((CollectionType) collection_in_a.get_type()).get_content_type()
      );

      types_in.add
      (
         ((CollectionType) collection_in_b.get_type()).get_content_type()
      );

      for (final Computation c: extra_params)
      {
         types_in.add(c.get_type());
      }

      RecurrentChecks.assert_lambda_matches_types(lambda_function, types_in);

      return
         new IndexedMergeComputation
         (
            origin,
            lambda_function,
            collection_in_a,
            collection_in_b,
            to_set,
            extra_params,
            CollectionType.build
            (
               origin,
               ((LambdaType) lambda_function.get_type()).get_return_type(),
               to_set,
               "auto generated"
            )
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_indexed_merge(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Computation get_collection_in_a ()
   {
      return collection_in_a;
   }

   public Computation get_collection_in_b ()
   {
      return collection_in_b;
   }

   public List<Computation> get_extra_parameters ()
   {
      return extra_params;
   }

   public boolean to_set ()
   {
      return to_set;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (to_set)
      {
         sb.append("(IndexedMergeToSet ");
      }
      else
      {
         sb.append("(IndexedMergeToList ");
      }

      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection_in_a.toString());
      sb.append(" ");

      sb.append(collection_in_b.toString());

      for (final Computation c: extra_params)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}