package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Sort extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:sort");

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
      base_param_types.add(Type.INT);

      if (call_parameters.size() != 2)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "("
               + alias
               + "! <(LAMBDA INT (X))> <(LIST X)|(SET X) REFERENCE>)"
            )
         );

         return null;
      }

      lambda_function = call_parameters.get(0);
      collection = call_parameters.get(1);

      collection.expect_non_string();

      RecurrentChecks.assert_is_a_list(collection);

      base_param_types.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      base_param_types.add(base_param_types.get(0));

      RecurrentChecks.propagate_expected_types_and_assert_is_lambda
      (
         lambda_function,
         base_param_types
      );

      RecurrentChecks.assert_return_type_is(lambda_function, Type.INT);

      collection.use_as_reference();

      return new Sort(origin, lambda_function, collection);
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
   protected Sort
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

      sb.append("(Sort ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
