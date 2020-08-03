package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class Cast extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Cast (final Computation parent, final Type to)
   {
      super(to);

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
      cv.visit_cast(this);
   }
   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Cast ");
      sb.append(parent.toString());
      sb.append(" ");
      sb.append(type.toString());
      sb.append(")");

      return sb.toString();
   }
}
