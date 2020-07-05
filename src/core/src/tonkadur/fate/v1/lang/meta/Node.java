package tonkadur.fate.v1.lang.meta;

import tonkadur.parser.Origin;

public abstract class Node
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Origin origin;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Node (final Origin origin)
   {
      this.origin = origin;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Origin get_origin ()
   {
      return origin;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(Node)");

      return sb.toString();
   }

   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof Node)
      {
         final Node in;

         in = (Node) o;

         return toString().equals(in.toString());
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return toString().hashCode();
   }
}
