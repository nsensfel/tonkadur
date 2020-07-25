package tonkadur.fate.v1.lang.instruction;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;

public class SequenceCall extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String sequence_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SequenceCall (final Origin origin, final String sequence_name)
   {
      super(origin);

      this.sequence_name = sequence_name;
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_sequence_call(this);
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

      sb.append("(SequenceCall ");
      sb.append(sequence_name);
      sb.append(")");

      return sb.toString();
   }
}
