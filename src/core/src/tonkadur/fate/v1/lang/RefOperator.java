package tonkadur.fate.v1.lang;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ValueNode;

public class RefOperator extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Variable variable;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RefOperator (final Origin origin, final Variable variable)
   {
      super(origin, new RefType(origin, variable.get_type(), "auto generated"));
      this.variable = variable;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /**** Accessors ************************************************************/

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(Ref ");
      sb.append(variable.get_name());
      sb.append(") ");

      return sb.toString();
   }
}
