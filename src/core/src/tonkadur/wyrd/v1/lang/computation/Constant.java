package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class Constant extends Computation
{
   public static final Constant TRUE;
   public static final Constant FALSE;

   public static final Constant ZERO;
   public static final Constant ONE;

   static
   {
      TRUE = new Constant(Type.BOOL, "true");
      FALSE = new Constant(Type.BOOL, "false");

      ZERO = new Constant(Type.INT, "0");
      ONE = new Constant(Type.INT, "1");
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String as_string;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Constant (final Type type, final String as_string)
   {
      super(type);

      this.as_string = as_string;
   }

   /**** Accessors ************************************************************/
   public String get_as_string ()
   {
      return as_string;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_constant(this);
   }
   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Constant ");
      sb.append(type.toString());
      sb.append(" ");
      sb.append(get_as_string());
      sb.append(")");

      return sb.toString();
   }
}
