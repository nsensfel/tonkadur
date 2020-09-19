package tonkadur.fate.v1.lang.computation;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class FilterComputation extends Computation
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
   protected FilterComputation
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection,
      final List<Computation> extra_params
   )
   {
      super(origin, collection.get_type());

      this.lambda_function = lambda_function;
      this.collection = collection;
      this.extra_params = extra_params;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static FilterComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection,
      final List<Computation> extra_params
   )
   throws ParsingError
   {
      final List<Type> target_signature;

      target_signature = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection);

      target_signature.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      for (final Computation c: extra_params)
      {
         target_signature.add(c.get_type());
      }

      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         Type.BOOL,
         target_signature
      );

      return
         new FilterComputation
         (
            origin,
            lambda_function,
            collection,
            extra_params
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_filter(this);
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

      sb.append("(Filter ");
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
