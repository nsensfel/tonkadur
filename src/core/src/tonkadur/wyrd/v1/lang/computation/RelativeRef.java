package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class RelativeRef extends Ref
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Ref parent;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RelativeRef
   (
      final Ref parent,
      final List<Computation> accesses,
      final Type target_type
   )
   {
      super(accesses, target_type);

      this.parent = parent;
   }

   /**** Accessors ************************************************************/
   public Ref get_parent ()
   {
      return parent;
   }
}
