package tonkadur.fate.v1.lang.computation;

import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class IndexedMapComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedMapComputation
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection,
      final List<Computation> extra_params,
      final Type output_type
   )
   {
      super(origin, output_type);

      this.lambda_function = lambda_function;
      this.collection = collection;
      this.extra_params = extra_params;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IndexedMapComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection,
      final List<Computation> extra_params
   )
   throws Throwable
   {
      final List<Type> in_types;

      in_types = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection);

      in_types.add(Type.INT);
      in_types.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      for (final Computation c: extra_params)
      {
         in_types.add(c.get_type());
      }

      RecurrentChecks.assert_lambda_matches_types(lambda_function, in_types);

      return
         new IndexedMapComputation
         (
            origin,
            lambda_function,
            collection,
            extra_params,
            CollectionType.build
            (
               origin,
               ((LambdaType) lambda_function.get_type()).get_return_type(),
               ((CollectionType) collection.get_type()).is_set(),
               "auto generated"
            )
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_indexed_map(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Computation get_collection ()
   {
      return collection;
   }

   public List<Computation> get_extra_parameters ()
   {
      return extra_params;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IndexedMap ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());

      for (final Computation c: extra_params)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
