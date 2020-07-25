package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Constant extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String as_string;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Constant (final Type type, final String as_string)
   {
      super(type);

      this.as_string = as_string;
   }

   /**** Accessors ************************************************************/
   public String get_as_string ()
   {
      return as_string;
   }
}
