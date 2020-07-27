package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;

public class PlayerChoiceList extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Instruction> choices;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public PlayerChoiceList
   (
      final Origin origin,
      final List<Instruction> choices
   )
   {
      super(origin);

      this.choices = choices;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_player_choice_list(this);
   }

   public List<Instruction> get_choices ()
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

      for (final Instruction choice: choices)
      {
         sb.append(choice.toString());
         sb.append(System.lineSeparator());
      }

      sb.append(")");

      return sb.toString();
   }
}
