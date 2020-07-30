package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class AddRichTextEffect extends RichText
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
   public AddRichTextEffect
   (
      final String effect_name,
      final List<Computation> effect_parameters,
      final List<Computation> content
   )
   {
      super(content);

      this.effect_name = effect_name;
      this.effect_parameters = effect_parameters;
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
}
