package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Ref extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> accesses;
   protected final Type target_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Ref (final List<Computation> accesses, final Type target_type)
   {
      super(Type.POINTER);

      this.accesses = accesses;
      this.target_type = target_type;
   }

   /**** Accessors ************************************************************/
   public List<Computation> get_accesses ()
   {
      return accesses;
   }

   public Type get_target_type ()
   {
      return target_type;
   }
}
