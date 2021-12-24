package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.PointerType;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class GetAllocableAddress extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type target_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public GetAllocableAddress (final Type target_type)
   {
      super(new PointerType(target_type));

      this.target_type = target_type;
   }

   /**** Accessors ************************************************************/
   public Type get_target_type ()
   {
      return target_type;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_get_allocable_address(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(GetAllocableAddress ");
      sb.append(target_type.toString());
      sb.append(")");

      return sb.toString();
   }
}
