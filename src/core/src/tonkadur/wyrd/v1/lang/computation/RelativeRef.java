package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class RelativeRef extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;
   protected final List<String> accesses;
   protected final Type target_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RelativeRef
   (
      final Computation parent,
      final List<String> accesses,
      final Type target_type
   )
   {
      super(Type.POINTER);

      this.parent = parent;
      this.accesses = accesses;
      this.target_type = target_type;
   }

   /**** Accessors ************************************************************/
   public Computation get_parent ()
   {
      return parent;
   }

   public List<String> get_accesses ()
   {
      return accesses;
   }

   public Type get_target_type ()
   {
      return target_type;
   }
}
