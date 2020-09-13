package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.instruction.AddElement;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class AddElementComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final AddElement instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AddElementComputation
   (
      final AddElement instruction
   )
   {
      super(instruction.get_origin(), instruction.get_collection().get_type());

      this.instruction = instruction;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AddElementComputation build
   (
      final Origin origin,
      final Computation element,
      final Computation collection
   )
   throws ParsingError
   {
      return
         new AddElementComputation
         (
            AddElement.build(origin, element, collection)
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_add_element(this);
   }

   public AddElement get_instruction ()
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
