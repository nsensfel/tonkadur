package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.InstructionNode;

public class InstructionList extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<InstructionNode> instructions;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public InstructionList
   (
      final Origin origin,
      final List<InstructionNode> instructions
   )
   {
      super(origin);

      this.instructions = instructions;
   }

   /**** Accessors ************************************************************/
   public List<InstructionNode> get_instructions ()
   {
      return instructions;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(InstructionList ");

      sb.append(System.lineSeparator());

      for (final InstructionNode instruction: instructions)
      {
         sb.append(instruction.toString());
         sb.append(System.lineSeparator());
      }

      sb.append(")");

      return sb.toString();
   }
}
