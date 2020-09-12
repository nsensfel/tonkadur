package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.instruction.Partition;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class PartitionComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Partition instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PartitionComputation
   (
      final Partition instruction,
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
   public static PartitionComputation build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in
   )
   throws Throwable
   {
      final Type type;
      final Partition parent;

      parent =
         Partition.build
         (
            origin,
            lambda_function,
            collection_in,
            null
         );

      type =
         new ConsType
         (
            origin,
            collection_in.get_type(),
            collection_in.get_type(),
            "auto generated"
         );

      return new PartitionComputation(parent, type);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_partition(this);
   }

   public Partition get_instruction ()
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
