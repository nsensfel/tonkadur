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

public class IndexedMerge extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Reference collection;
   protected final Computation default_a;
   protected final Computation collection_in_b;
   protected final Computation default_b;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedMerge
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in_b,
      final Computation default_b,
      final Reference collection,
      final Computation default_a,
      final List<Computation> extra_params
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection = collection;
      this.default_a = default_a;
      this.collection_in_b = collection_in_b;
      this.default_b = default_b;
      this.extra_params = extra_params;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IndexedMerge build
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in_b,
      final Computation default_b,
      final Reference collection,
      final Computation default_a,
      final List<Computation> extra_params
   )
   throws Throwable
   {
      final List<Type> types_in;

      types_in = new ArrayList<Type>();

      if (default_a == null)
      {
         RecurrentChecks.assert_is_a_collection(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_collection_of(collection, default_a);
      }

      if (default_b == null)
      {
         RecurrentChecks.assert_is_a_collection(collection_in_b);
      }
      else
      {
         RecurrentChecks.assert_is_a_collection_of(collection_in_b, default_b);
      }

      types_in.add(Type.INT);
      types_in.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      if (default_b != null)
      {
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

      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         ((CollectionType) collection.get_type()).get_content_type(),
         types_in
      );

      return
         new IndexedMerge
         (
            origin,
            lambda_function,
            collection_in_b,
            default_b,
            collection,
            default_a,
            extra_params
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_indexed_merge(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Computation get_main_default ()
   {
      return default_a;
   }

   public Computation get_secondary_collection ()
   {
      return collection_in_b;
   }

   public Computation get_secondary_default ()
   {
      return default_b;
   }

   public Reference get_main_collection ()
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

      sb.append("(IndexedMerge ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());
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
