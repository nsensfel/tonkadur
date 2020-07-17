package tonkadur.fate.v1.lang.meta;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

public abstract class ValueNode extends Node
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type type;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ValueNode (final Origin origin, final Type type)
   {
      super(origin);

      this.type = type;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Type get_type ()
   {
      return type;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(");
      sb.append(type.get_name());
      sb.append(" Value Node)");

      return sb.toString();
   }
}
