package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.RichTextNode;

public class PlayerChoice extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final RichTextNode text;
   protected final List<InstructionNode> effects;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public PlayerChoice
   (
      final Origin origin,
      final RichTextNode text,
      final List<InstructionNode> effects
   )
   {
      super(origin);

      this.text = text;
      this.effects = effects;
   }


   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_player_choice(this);
   }

   public RichTextNode get_text ()
   {
      return text;
   }

   public List<InstructionNode> get_effects ()
   {
      return effects;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(PlayerChoice");
      sb.append(System.lineSeparator());
      sb.append(text.toString());

      for (final InstructionNode effect: effects)
      {
         sb.append(System.lineSeparator());
         sb.append(effect.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
