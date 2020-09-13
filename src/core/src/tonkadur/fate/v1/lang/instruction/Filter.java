package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;

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

public class Filter extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Filter
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Filter build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection
   )
   throws ParsingError
   {

      RecurrentChecks.assert_is_a_collection(collection);
      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         Type.BOOL,
         Collections.singletonList
         (
            ((CollectionType) collection.get_type()).get_content_type()
         )
      );

      return new Filter(origin, lambda_function, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_filter(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Reference get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Filter ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
