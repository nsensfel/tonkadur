package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Size extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Ref collection;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Size (final Ref collection)
   {
      super(Type.INT);

      this.collection = collection;
   }

   /**** Accessors ************************************************************/
   public Ref get_collection ()
   {
      return collection;
   }
}