package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class AbsoluteRef extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<String> accesses;
   protected final Type target_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AbsoluteRef (final List<String> accesses, final Type target_type)
   {
      super(Type.POINTER);

      this.accesses = accesses;
      this.target_type = target_type;
   }

   /**** Accessors ************************************************************/
   public List<String> get_accesses ()
   {
      return accesses;
   }

   public Type get_target_type ()
   {
      return target_type;
   }
}
