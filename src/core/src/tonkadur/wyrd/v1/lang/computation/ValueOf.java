package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class ValueOf extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ValueOf (final Computation parent, final Type to)
   {
      super(to);

      this.parent = parent;
   }

   /**** Accessors ************************************************************/
   public Computation get_parent ()
   {
      return parent;
   }
}
