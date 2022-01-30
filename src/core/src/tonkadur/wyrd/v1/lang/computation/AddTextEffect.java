package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.optimizer.TextConstantOptimizer;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class AddTextEffect extends Text
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String effect_name;
   protected final List<Computation> effect_parameters;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddTextEffect
   (
      final String effect_name,
      final List<Computation> effect_parameters,
      final List<Computation> content
   )
   {
      super(content);

      this.effect_name = effect_name;
      this.effect_parameters = effect_parameters;

      TextConstantOptimizer.optimize(content);
   }

   /**** Accessors ************************************************************/
   public String get_effect_name ()
   {
      return effect_name;
   }

   public List<Computation> get_effect_parameters ()
   {
      return effect_parameters;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_add_text_effect(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(AddTextEffect (");
      sb.append(effect_name);

      for (final Computation param: effect_parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(")");

      for (final Computation text: content)
      {
         sb.append(" ");
         sb.append(text.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
