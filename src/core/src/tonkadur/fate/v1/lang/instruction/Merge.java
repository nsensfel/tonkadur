package tonkadur.fate.v1.lang.instruction;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class Merge extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Reference collection_in_a;
   protected final Computation default_a;
   protected final Reference collection_in_b;
   protected final Computation default_b;
   protected final Reference collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Merge
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in_a,
      final Computation default_a,
      final Reference collection_in_b,
      final Computation default_b,
      final Reference collection_out
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection_in_a = collection_in_a;
      this.default_a = default_a;
      this.collection_in_b = collection_in_b;
      this.default_b = default_b;
      this.collection_out = collection_out;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Merge build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in_a,
      final Computation default_a,
      final Reference collection_in_b,
      final Computation default_b,
      final Reference collection_out
   )
   throws Throwable
   {
      final List<Type> types_in;

      types_in = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection_out);

      if (default_a == null)
      {
         RecurrentChecks.assert_is_a_collection(collection_in_a);
      }
      else
      {
         RecurrentChecks.assert_is_a_collection_of(collection_in_a, default_a);
      }

      if (default_b == null)
      {
         RecurrentChecks.assert_is_a_collection(collection_in_b);
      }
      else
      {
         RecurrentChecks.assert_is_a_collection_of(collection_in_b, default_b);
      }

      types_in.add
      (
         ((CollectionType) collection_in_a.get_type()).get_content_type()
      );

      types_in.add
      (
         ((CollectionType) collection_in_b.get_type()).get_content_type()
      );

      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         ((CollectionType) collection_out.get_type()).get_content_type(),
         types_in
      );

      return
         new Merge
         (
            origin,
            lambda_function,
            collection_in_a,
            default_a,
            collection_in_b,
            default_b,
            collection_out
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_merge(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Reference get_collection_in_a ()
   {
      return collection_in_a;
   }

   public Computation get_default_a ()
   {
      return default_a;
   }

   public Reference get_collection_in_b ()
   {
      return collection_in_b;
   }

   public Computation get_default_b ()
   {
      return default_b;
   }

   public Reference get_collection_out ()
   {
      return collection_out;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Merge ");
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

      sb.append(" ");

      if (collection_out == null)
      {
         sb.append("null");
      }
      else
      {
         sb.append(collection_out.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
