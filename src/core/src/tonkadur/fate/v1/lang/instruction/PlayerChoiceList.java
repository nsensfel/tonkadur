package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.InstructionNode;

public class PlayerChoiceList extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<InstructionNode> choices;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public PlayerChoiceList
   (
      final Origin origin,
      final List<InstructionNode> choices
   )
   {
      super(origin);

      this.choices = choices;
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_player_choice_list(this);
   }

   public List<InstructionNode> get_choices ()
   {
      return choices;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(PlayerChoiceList ");

      sb.append(System.lineSeparator());

      for (final InstructionNode choice: choices)
      {
         sb.append(choice.toString());
         sb.append(System.lineSeparator());
      }

      sb.append(")");

      return sb.toString();
   }
}
