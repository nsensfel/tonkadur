package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class IndexedFilter extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedFilter
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection,
      final List<Computation> extra_params
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection = collection;
      this.extra_params = extra_params;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IndexedFilter build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection,
      final List<Computation> extra_params
   )
   throws ParsingError
   {
      final List<Type> target_signature;

      target_signature = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection);

      target_signature.add(Type.INT);
      target_signature.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
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

      return new IndexedFilter(origin, lambda_function, collection, extra_params);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_indexed_filter(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Reference get_collection ()
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

      sb.append("(IndexedFilter ");
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
