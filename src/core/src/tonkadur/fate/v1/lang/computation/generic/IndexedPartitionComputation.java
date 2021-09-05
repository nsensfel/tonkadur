package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class IndexedPartitionComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:indexed_partition");
      aliases.add("list:indexedpartition");
      aliases.add("list:indexedPartition");
      aliases.add("list:ipartition");
      aliases.add("set:indexed_partition");
      aliases.add("set:indexedpartition");
      aliases.add("set:indexedPartition");
      aliases.add("set:ipartition");

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
      final Computation collection;
      final List<Computation> extra_params;
      final List<Type> base_param_types;
      final Type type;

      base_param_types = new ArrayList<Type>();

      if (call_parameters.size() < 2)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "("
               + alias
               + " <(LAMBDA BOOL (INT X Y0...YN))> <(LIST X)|(SET X)>"
               + " <Y0>...<YN>)"
            )
         );

         return null;
      }

      lambda_function = call_parameters.get(0);
      collection = call_parameters.get(1);
      extra_params = call_parameters.subList(2, call_parameters.size());

      collection.expect_non_string();

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      base_param_types.add(Type.INT);

      base_param_types.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      RecurrentChecks.propagate_expected_types_and_assert_is_lambda
      (
         lambda_function,
         base_param_types,
         extra_params
      );

      RecurrentChecks.assert_return_type_is(lambda_function, Type.BOOL);

      type =
         new ConsType
         (
            origin,
            collection.get_type(),
            collection.get_type(),
            "auto generated"
         );

      return
         new IndexedPartitionComputation
         (
            origin,
            lambda_function,
            collection,
            extra_params,
            type
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
   protected IndexedPartitionComputation
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

      sb.append("(IndexedPartition ");
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
