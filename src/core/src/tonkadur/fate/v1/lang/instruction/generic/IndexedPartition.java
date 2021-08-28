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

public class IndexedPartition extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

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

   public static Instruction build
   (
      final Origin origin,
      final String _alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      // TODO: implement
      final Computation lambda_function = null;
      final Computation collection_in = null;
      final Computation collection_out = null;
      final List<Computation> extra_params = null;

      final List<Type> target_signature;

      target_signature = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection_in);
      RecurrentChecks.assert_is_a_collection(collection_out);
      RecurrentChecks.assert_can_be_used_as
      (
         collection_in,
         collection_out.get_type()
      );

      target_signature.add(Type.INT);
      target_signature.add
      (
         ((CollectionType) collection_in.get_type()).get_content_type()
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
         new IndexedPartition
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
   protected IndexedPartition
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

      sb.append("(IndexedPartition ");
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
