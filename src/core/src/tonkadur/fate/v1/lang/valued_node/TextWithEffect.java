package tonkadur.fate.v1.lang.valued_node;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.TextEffect;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.TextNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class TextWithEffect extends TextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final TextEffect effect;
   protected final List<ValueNode> parameters;
   protected final TextNode text;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected TextWithEffect
   (
      final Origin origin,
      final TextEffect effect,
      final List<ValueNode> parameters,
      final TextNode text
   )
   {
      super(origin);

      this.effect = effect;
      this.parameters = parameters;
      this.text = text;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public TextWithEffect
   (
      final Origin origin,
      final TextEffect effect,
      final TextNode text
   )
   {
      super(origin);

      this.effect = effect;
      this.parameters = new ArrayList<ValueNode>();
      this.text = text;
   }

   public static TextWithEffect build
   (
      final Origin origin,
      final TextEffect effect,
      final List<ValueNode> parameters,
      final TextNode text
   )
   {
      /* TODO: Checks */
      return new TextWithEffect(origin, effect, parameters, text);
   }

   /**** Accessors ************************************************************/
   public TextEffect get_effect ()
   {
      return effect;
   }

   public List<ValueNode> get_parameters ()
   {
      return parameters;
   }

   public TextNode get_text ()
   {
      return text;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(TextWithEffect (");
      sb.append(effect.get_name());

      for (final ValueNode param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(") ");
      sb.append(text.toString());
      sb.append(")");

      return sb.toString();
   }
}
