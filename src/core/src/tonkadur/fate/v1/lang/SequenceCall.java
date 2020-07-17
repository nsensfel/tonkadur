package tonkadur.fate.v1.lang;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.InstructionNode;

public class SequenceCall extends InstructionNode
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
