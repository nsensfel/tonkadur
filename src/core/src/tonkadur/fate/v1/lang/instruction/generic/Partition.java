package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Partition extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:partition");
      aliases.add("set:partition");

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
      final Computation collection_in;
      final Computation collection_out;
      final List<Computation> extra_params;

      if (call_parameters.size() < 3)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      lambda_function = call_parameters.get(0);
      collection_in = call_parameters.get(1);
      collection_out = call_parameters.get(2);
      extra_params = call_parameters.subList(3, call_parameters.size());

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection_in);
         RecurrentChecks.assert_is_a_set(collection_out);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection_in);
         RecurrentChecks.assert_is_a_list(collection_out);
      }

      RecurrentChecks.assert_can_be_used_as
      (
         collection_in,
         collection_out.get_type()
      );

      RecurrentChecks.propagate_expected_types_and_assert_is_lambda
      (
         lambda_function,
         Collections.singletonList
         (
            ((CollectionType) collection_in.get_type()).get_content_type()
         ),
         extra_params
      );

      RecurrentChecks.assert_return_type_is(lambda_function, Type.BOOL);

      collection_out.use_as_reference();

      return
         new Partition
         (
            origin,
            lambda_function,
            collection_in,
            collection_out,
            extra_params
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Computation collection_in;
   protected final Computation collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Partition
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in,
      final Computation collection_out,
      final List<Computation> extra_params
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection_in = collection_in;
      this.collection_out = collection_out;
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

   public Computation get_collection_in ()
   {
      return collection_in;
   }

   public Computation get_collection_out ()
   {
      return collection_out;
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

      sb.append("(Partition ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection_in.toString());
      sb.append(" ");
      sb.append(collection_out.toString());

      for (final Computation c: extra_params)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
