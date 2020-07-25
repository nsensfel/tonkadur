package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Operation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String operator;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Operation
   (
      final Type result_type,
      final String operator,
      final List<Computation> parameters
   )
   {
      super(result_type);

      this.operator = operator;
      this.parameters = parameters;
   }

   /**** Accessors ************************************************************/
   public List<Computation> get_parameters ()
   {
      return parameters;
   }
}
