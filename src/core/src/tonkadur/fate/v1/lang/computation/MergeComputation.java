package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.instruction.Merge;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class MergeComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Merge instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected MergeComputation
   (
      final Merge instruction,
      final Type output_type
   )
   {
      super(instruction.get_origin(), output_type);

      this.instruction = instruction;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static MergeComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in_a,
      final Computation default_a,
      final Reference collection_in_b,
      final Computation default_b
   )
   throws Throwable
   {
      final Type type;
      final Merge parent;

      parent =
         Merge.build
         (
            origin,
            lambda_function,
            collection_in_a,
            default_a,
            collection_in_b,
            default_b,
            null
         );

      type =
         CollectionType.build
         (
            origin,
            ((LambdaType) lambda_function.get_type()).get_return_type(),
            (
               ((CollectionType) collection_in_a.get_type()).is_set()
               || ((CollectionType) collection_in_b.get_type()).is_set()
            ),
            "auto generated"
         );

      return new MergeComputation(parent, type);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_merge(this);
   }

   public Merge get_instruction ()
   {
      return instruction;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ComputationOf ");
      sb.append(instruction.toString());

      sb.append(")");

      return sb.toString();
   }
}
