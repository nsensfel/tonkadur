package tonkadur.wyrd.v1.lang.meta;

import tonkadur.wyrd.v1.lang.type.Type;

public abstract class Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type type;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Computation (final Type type)
   {
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
}