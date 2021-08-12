package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collection;
import java.util.Collections;
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

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class FilterComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:filter");
      aliases.add("set:filter");

      return aliases;
   }

   public static GenericComputation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation lambda_function;
      final Computation collection;
      final List<Computation> extra_params;
      final List<Type> signature;

      if (call_parameters.size() < 2)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      lambda_function = call_parameters.get(0);
      collection = call_parameters.get(1);

      if (call_parameters.size() == 2)
      {
         extra_params = new ArrayList<Computation>();
      }
      else
      {
         extra_params = call_parameters.subList(2, call_parameters.size());
      }

      lambda_function.expect_non_string();
      collection.expect_non_string();

      RecurrentChecks.assert_is_a_lambda_function(lambda_function);

      signature = ((LambdaType) lambda_function.get_type()).get_signature();

      if (signature.size() < 1)
      {
         // TODO: Error.
         System.err.println
         (
            "Lambda signature too small at "
            + lambda_function.get_origin().toString()
         );

         return null;
      }

      if (signature.size() > 2)
      {
         RecurrentChecks.propagate_expected_types_and_assert_computations_matches_signature
         (
            origin,
            extra_params,
            signature.subList(1, signature.size())
         );
      }

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set_of(collection, signature.get(0));
      }
      else
      {
         RecurrentChecks.assert_is_a_list_of(collection, signature.get(0));
      }

      RecurrentChecks.assert_can_be_used_as
      (
         lambda_function.get_origin(),
         ((LambdaType) lambda_function.get_type()).get_return_type(),
         Type.BOOL
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
   /**** Accessors ************************************************************/
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
