package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Size extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Size
   (
      final Computation collection
   )
   {
      super(Type.INT);

      this.collection = collection;
   }

   /**** Accessors ************************************************************/
   public Computation get_collection ()
   {
      return collection;
   }
}
