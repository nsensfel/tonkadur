package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collection;
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

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class Fold extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:foldl");
      aliases.add("list:foldr");
      aliases.add("list:fold_left");
      aliases.add("list:foldleft");
      aliases.add("list:foldLeft");
      aliases.add("list:fold_right");
      aliases.add("list:foldright");
      aliases.add("list:foldRight");

      aliases.add("set:foldl");
      aliases.add("set:foldr");
      aliases.add("set:fold_left");
      aliases.add("set:foldleft");
      aliases.add("set:foldLeft");
      aliases.add("set:fold_right");
      aliases.add("set:foldright");
      aliases.add("set:foldRight");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation lambda_function;
      final Computation initial_value;
      final Computation collection;
      final boolean is_foldl;
      final List<Computation> extra_params;
      final List<Type> signature;
      final List<Type> expected_signature;

      if (call_parameters.size() < 3)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      lambda_function = call_parameters.get(0);
      initial_value = call_parameters.get(1);
      collection = call_parameters.get(2);

      lambda_function.expect_non_string();
      collection.expect_non_string();

      is_foldl = (alias.contains("foldl") || alias.contains("eft"));

      if (call_parameters.size() == 3)
      {
         extra_params = new ArrayList<Computation>();
      }
      else
      {
         extra_params = call_parameters.subList(3, call_parameters.size());
      }

      signature = ((LambdaType) lambda_function.get_type()).get_signature();

      if (signature.size() < 2)
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
            signature.subList(2, signature.size())
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

      RecurrentChecks.handle_expected_type_propagation
      (
         initial_value,
         signature.get(1)
      );

      RecurrentChecks.assert_can_be_used_as
      (
         initial_value,
         signature.get(1)
      );

      RecurrentChecks.assert_can_be_used_as
      (
         lambda_function.get_origin(),
         ((LambdaType) lambda_function.get_type()).get_return_type(),
         initial_value.get_type()
      );

      return
         new Fold
         (
            origin,
            lambda_function,
            initial_value,
            collection,
            is_foldl,
            extra_params,
            initial_value.get_type()
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Computation initial_value;
   protected final Computation collection;
   protected final boolean is_foldl;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Fold
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation initial_value,
      final Computation collection,
      final boolean is_foldl,
      final List<Computation> extra_params,
      final Type act_as
   )
   {
      super(origin, act_as);

      this.lambda_function = lambda_function;
      this.initial_value = initial_value;
      this.collection = collection;
      this.is_foldl = is_foldl;
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

   public Computation get_initial_value ()
   {
      return initial_value;
   }

   public Computation get_collection ()
   {
      return collection;
   }

   public boolean is_foldl ()
   {
      return is_foldl;
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

      if (is_foldl)
      {
         sb.append("(Foldl ");
      }
      else
      {
         sb.append("(Foldr ");
      }

      sb.append(lambda_function.toString());

      sb.append(" ");
      sb.append(initial_value.toString());
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
