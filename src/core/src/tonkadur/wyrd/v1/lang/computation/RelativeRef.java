package tonkadur.wyrd.v1.lang.computation;

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
      final Computation member,
      final Type target_type
   )
   {
      super(member, target_type);

      this.parent = parent;
   }

   /**** Accessors ************************************************************/
   public Ref get_parent ()
   {
      return parent;
   }
}
