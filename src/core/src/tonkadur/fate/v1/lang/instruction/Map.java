package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
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

public class Map extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Computation collection_in;
   protected final Reference collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Map
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in,
      final Reference collection_out
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection_in = collection_in;
      this.collection_out = collection_out;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Map build
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection_in,
      final Reference collection_out
   )
   throws ParsingError
   {
      RecurrentChecks.assert_is_a_collection(collection_in);
      RecurrentChecks.assert_is_a_collection(collection_out);
      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         ((CollectionType) collection_out.get_type()).get_content_type(),
         Collections.singletonList
         (
            ((CollectionType) collection_in.get_type()).get_content_type()
         )
      );

      return new Map(origin, lambda_function, collection_in, collection_out);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_map(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Computation get_collection_in ()
   {
      return collection_in;
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

      sb.append("(Map ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection_in.toString());
      sb.append(" ");
      sb.append(collection_out.toString());
      sb.append(")");

      return sb.toString();
   }
}
