package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.instruction.RemoveElementAt;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class RemoveElementAtComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final RemoveElementAt instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected RemoveElementAtComputation
   (
      final RemoveElementAt instruction
   )
   {
      super(instruction.get_origin(), instruction.get_collection().get_type());

      this.instruction = instruction;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static RemoveElementAtComputation build
   (
      final Origin origin,
      final Computation index,
      final Reference collection
   )
   throws
      InvalidTypeException,
      IncomparableTypeException
   {
      return
         new RemoveElementAtComputation
         (
            RemoveElementAt.build(origin, index, collection)
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_remove_element_at(this);
   }

   public RemoveElementAt get_instruction ()
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
