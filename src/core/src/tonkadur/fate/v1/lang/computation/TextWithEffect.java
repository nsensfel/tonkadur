package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.TextEffect;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.TextNode;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class TextWithEffect extends TextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final TextEffect effect;
   protected final List<Computation> parameters;
   protected final TextNode text;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected TextWithEffect
   (
      final Origin origin,
      final TextEffect effect,
      final List<Computation> parameters,
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
   public static TextWithEffect build
   (
      final Origin origin,
      final TextEffect effect,
      final List<Computation> parameters,
      final TextNode text
   )
   throws ParsingError
   {
      RecurrentChecks.assert_computations_matches_signature
      (
         origin,
         parameters,
         effect.get_signature()
      );

      return new TextWithEffect(origin, effect, parameters, text);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_text_with_effect(this);
   }

   public TextEffect get_effect ()
   {
      return effect;
   }

   public List<Computation> get_parameters ()
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

      for (final Computation param: parameters)
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
