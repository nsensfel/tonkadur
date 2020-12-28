package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.SequenceType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class SequenceReference extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String sequence_name;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SequenceReference
   (
      final Origin origin,
      final String sequence_name
   )
   {
      super(origin, new SequenceType(origin, "(seq " + sequence_name + ")"));

      this.sequence_name = sequence_name;
   }


   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_sequence_reference(this);
   }

   public String get_sequence_name ()
   {
      return sequence_name;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(SequenceReference ");
      sb.append(sequence_name);

      sb.append(")");

      return sb.toString();
   }
}
