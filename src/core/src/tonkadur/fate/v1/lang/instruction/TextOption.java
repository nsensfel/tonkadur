package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.TextNode;

public class TextOption extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final TextNode text;
   protected final List<Instruction> effects;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public TextOption
   (
      final Origin origin,
      final TextNode text,
      final List<Instruction> effects
   )
   {
      super(origin);

      this.text = text;
      this.effects = effects;
   }


   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_text_option(this);
   }

   public TextNode get_text ()
   {
      return text;
   }

   public List<Instruction> get_effects ()
   {
      return effects;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(TextOption (");
      sb.append(System.lineSeparator());
      sb.append(text.toString());
      sb.append(")");

      for (final Instruction effect: effects)
      {
         sb.append(System.lineSeparator());
         sb.append(effect.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
