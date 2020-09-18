package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class Constant extends Computation
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
   public static Constant build_boolean
   (
      final Origin origin,
      final boolean value
   )
   {
      return new Constant(origin, Type.BOOL, value ? "true" : "false");
   }

   public static Constant build_string
   (
      final Origin origin,
      final String value
   )
   {
      return new Constant(origin, Type.STRING, value);
   }

   public static Constant build (final Origin origin, final String as_string)
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

      return new Constant(origin, Type.STRING, as_string);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_constant(this);
   }

   public String get_value_as_string ()
   {
      return as_string;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(");
      sb.append(type.get_name());
      sb.append(" Constant ");
      sb.append(as_string);
      sb.append(")");

      return sb.toString();
   }
}
