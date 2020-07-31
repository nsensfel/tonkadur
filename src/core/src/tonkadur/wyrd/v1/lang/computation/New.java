package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class New extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type target_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public New (final Type target_type)
   {
      super(Type.POINTER);

      this.target_type = target_type;
   }

   /**** Accessors ************************************************************/
   public Type get_target_type ()
   {
      return target_type;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(New ");
      sb.append(target_type.toString());
      sb.append(")");

      return sb.toString();
   }
}
