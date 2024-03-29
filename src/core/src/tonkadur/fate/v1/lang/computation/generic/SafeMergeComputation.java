package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class SafeMergeComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:safe_merge");
      aliases.add("list:safemerge");
      aliases.add("list:safeMerge");
      aliases.add("set:safe_merge");
      aliases.add("set:safemerge");
      aliases.add("set:safeMerge");

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
      final Computation collection_a;
      final Computation default_a;
      final Computation collection_b;
      final Computation default_b;
      final List<Type> base_param_types;
      final boolean to_set;

      base_param_types = new ArrayList<Type>();

      if (call_parameters.size() != 5)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "("
               + alias
               + " <(LAMBDA X (Y Z))> <(LIST Y)|(SET Y)>"
               + " <default_y: Y> <(LIST Z)|(SET Z)> <default_z: Z>)"
            )
         );

         return null;
      }

      lambda_function = call_parameters.get(0);
      collection_a = call_parameters.get(1);
      default_a = call_parameters.get(2);
      collection_b = call_parameters.get(3);
      default_b = call_parameters.get(4);

      RecurrentChecks.propagate_expected_types_and_assert_is_a_collection_of
      (
         collection_a,
         default_a
      );

      RecurrentChecks.propagate_expected_types_and_assert_is_a_collection_of
      (
         collection_a,
         default_b
      );

      base_param_types.add
      (
         ((CollectionType) collection_a.get_type()).get_content_type()
      );

      base_param_types.add
      (
         ((CollectionType) collection_b.get_type()).get_content_type()
      );

      RecurrentChecks.propagate_expected_types_and_assert_is_lambda
      (
         lambda_function,
         base_param_types
      );

      to_set = alias.startsWith("set:");

      if (to_set)
      {
         RecurrentChecks.assert_can_be_used_in_set
         (
            lambda_function.get_origin().with_hint("returned value"),
            ((LambdaType) lambda_function.get_type()).get_return_type()
         );
      }

      return
         new SafeMergeComputation
         (
            origin,
            lambda_function,
            collection_a,
            default_a,
            collection_b,
            default_b,
            to_set,
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
   protected SafeMergeComputation
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in_a,
      final Computation default_a,
      final Computation collection_in_b,
      final Computation default_b,
      final boolean to_set,
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
         sb.append("(SafeMergeToSet ");
      }
      else
      {
         sb.append("(SafeMergeToList ");
      }

      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection_in_a.toString());

      sb.append(" ");
      sb.append(default_a.toString());

      sb.append(" ");
      sb.append(collection_in_b.toString());

      sb.append(" ");
      sb.append(default_b.toString());
      sb.append(")");

      return sb.toString();
   }
}
