package tonkadur.wyrd.v1.lang.computation;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class IfElseComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Computation if_true;
   protected final Computation if_false;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public IfElseComputation
   (
      final Computation condition,
      final Computation if_true,
      final Computation if_false
   )
   {
      super(if_true.get_type());

      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /**** Accessors ************************************************************/
   public Computation get_condition ()
   {
      return condition;
   }

   public Computation get_if_true ()
   {
      return if_true;
   }

   public Computation get_if_false ()
   {
      return if_false;
   }
}
