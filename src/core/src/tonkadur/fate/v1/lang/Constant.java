package tonkadur.fate.v1.lang;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ValueNode;

public class Constant extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String as_string;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Constant
   (
      final Origin origin,
      final Type result_type,
      final String as_string
   )
   {
      super(origin, result_type);

      this.as_string = as_string;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Constant build (final Origin origin, final String as_string)
   {
      try
      {
         Integer.valueOf(as_string);

         return new Constant(origin, Type.INT, as_string);
      }
      catch (final NumberFormatException nfe)
      {
         /* That's fine, we're just testing... */
      }

      try
      {
         Float.valueOf(as_string);

         return new Constant(origin, Type.FLOAT, as_string);
      }
      catch (final NumberFormatException nfe)
      {
         /* That's fine, we're just testing... */
      }

      try
      {
         Boolean.valueOf(as_string);

         return new Constant(origin, Type.BOOLEAN, as_string);
      }
      catch (final NumberFormatException nfe)
      {
         /* That's fine, we're just testing... */
      }

      return new Constant(origin, Type.STRING, as_string);
   }

   /**** Accessors ************************************************************/
   public String get_value_as_string ()
   {
      return as_string;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(");
      sb.append(type.get_name());
      sb.append(" Constant ");
      sb.append(as_string);
      sb.append(")");

      return sb.toString();
   }
}
