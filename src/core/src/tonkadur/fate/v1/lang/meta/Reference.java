package tonkadur.fate.v1.lang.meta;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

public abstract class Reference extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Reference (final Origin origin, final Type type, final String name)
   {
      super(origin, type);

      this.name = name;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public String get_name ()
   {
      return name;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(");
      sb.append(type.get_name());
      sb.append(" Reference ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }
}
