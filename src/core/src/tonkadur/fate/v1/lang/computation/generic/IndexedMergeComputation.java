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

public class IndexedMergeComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:indexed_merge");
      aliases.add("list:indexedmerge");
      aliases.add("list:indexedMerge");
      aliases.add("list:imerge");
      aliases.add("set:indexed_merge");
      aliases.add("set:indexedmerge");
      aliases.add("set:indexedMerge");
      aliases.add("set:imerge");

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
      // TODO: implement
      final Computation lambda_function = null;
      final Computation collection_in_a = null;
      final Computation default_a = null;
      final Computation collection_in_b = null;
      final Computation default_b = null;
      final boolean to_set = false;
      final List<Computation> extra_params = null;
      final List<Type> types_in;

      types_in = new ArrayList<Type>();

      if (default_a == null)
      {
         RecurrentChecks.assert_is_a_collection(collection_in_a);
      }
      else
      {
         RecurrentChecks.propagate_expected_types_and_assert_is_a_collection_of
         (
            collection_in_a,
            default_a
         );
      }

      if (default_b == null)
      {
         RecurrentChecks.assert_is_a_collection(collection_in_b);
      }
      else
      {
         RecurrentChecks.propagate_expected_types_and_assert_is_a_collection_of
         (
            collection_in_b,
            default_b
         );
      }

      types_in.add(Type.INT);

      types_in.add
      (
         ((CollectionType) collection_in_a.get_type()).get_content_type()
      );

      if (default_b != null)
      {
         /*
          * Safe-Mode: two indices.
          * Unsafe-Mode: only one index, since out-of-bound means stopping.
          */
         types_in.add(Type.INT);
      }

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
            default_a,
            collection_in_b,
            default_b,
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

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Computation collection_in_a;
   protected final Computation default_a;
   protected final Computation collection_in_b;
   protected final Computation default_b;
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
      final Computation default_a,
      final Computation collection_in_b,
      final Computation default_b,
      final boolean to_set,
      final List<Computation> extra_params,
      final Type output_type
   )
   {
      super(origin, output_type);

      this.lambda_function = lambda_function;
      this.collection_in_a = collection_in_a;
      this.default_a = default_a;
      this.collection_in_b = collection_in_b;
      this.default_b = default_b;
      this.to_set = to_set;
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

   public Computation get_collection_in_a ()
   {
      return collection_in_a;
   }

   public Computation get_default_a ()
   {
      return default_a;
   }

   public Computation get_collection_in_b ()
   {
      return collection_in_b;
   }

   public Computation get_default_b ()
   {
      return default_b;
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

      if (default_a == null)
      {
         sb.append("null");
      }
      else
      {
         sb.append(default_a.toString());
      }

      sb.append(" ");
      sb.append(collection_in_b.toString());
      sb.append(" ");

      if (default_b == null)
      {
         sb.append("null");
      }
      else
      {
         sb.append(default_b.toString());
      }

      for (final Computation c: extra_params)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
