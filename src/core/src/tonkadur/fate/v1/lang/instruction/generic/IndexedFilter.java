package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class IndexedFilter extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:indexed_filter");
      aliases.add("list:indexedfilter");
      aliases.add("list:indexedFilter");
      aliases.add("list:ifilter");
      aliases.add("set:indexed_filter");
      aliases.add("set:indexedfilter");
      aliases.add("set:indexedFilter");
      aliases.add("set:ifilter");

      return aliases;
   }

   public static Instruction build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation lambda_function;
      final Computation collection;
      final List<Type> base_param_types;

      base_param_types = new ArrayList<Type>();

      if (call_parameters.size() != 2)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "("
               + alias
               + "! <(LAMBDA BOOL (INT X))>"
               + " <(LIST X)|(SET X) REFERENCE>)"
            )
         );

         return null;
      }

      lambda_function = call_parameters.get(0);
      collection = call_parameters.get(1);

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
         base_param_types
      );

      RecurrentChecks.assert_return_type_is(lambda_function, Type.BOOL);

      collection.use_as_reference();

      return
         new IndexedFilter
         (
            origin,
            lambda_function,
            collection
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedFilter
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection = collection;
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

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IndexedFilter ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
