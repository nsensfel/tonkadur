package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.RefType;

import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class RefOperator extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference referred;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public RefOperator (final Origin origin, final Reference referred)
   {
      super(origin, new RefType(origin, referred.get_type(), "auto generated"));
      this.referred = referred;
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
      sb.append(referred.get_name());
      sb.append(") ");

      return sb.toString();
   }
}
