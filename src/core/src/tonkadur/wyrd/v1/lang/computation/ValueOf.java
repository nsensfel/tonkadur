package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

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

   public ValueOf (final Address parent)
   {
      super(parent.get_target_type());

      this.parent = parent;
   }

   /**** Accessors ************************************************************/
   public Computation get_parent ()
   {
      return parent;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_value_of(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(ValueOf ");
      sb.append(parent.toString());
      sb.append(")");

      return sb.toString();
   }
}
