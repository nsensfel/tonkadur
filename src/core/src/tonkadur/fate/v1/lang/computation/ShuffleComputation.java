package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.instruction.Shuffle;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class ShuffleComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Shuffle instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ShuffleComputation
   (
      final Shuffle instruction
   )
   {
      super(instruction.get_origin(), instruction.get_collection().get_type());

      this.instruction = instruction;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ShuffleComputation build
   (
      final Origin origin,
      final Computation collection
   )
   throws InvalidTypeException
   {
      /*
       * FIXME: this computation should accept any collection type, and return a
       * list, which is not the case of Shuffle.build
       */
      return new ShuffleComputation(Shuffle.build(origin, collection));
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_shuffle(this);
   }

   public Shuffle get_instruction ()
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
