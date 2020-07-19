package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.TextNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class ValueToText extends TextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode value;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ValueToText (final ValueNode value)
   {
      super(value.get_origin());

      this.value = value;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ValueToText build (final ValueNode value)
   throws
      IncompatibleTypeException,
      IncomparableTypeException
   {
      final Type value_base_type;

      value_base_type = value.get_type().get_base_type();

      if (value_base_type.equals(Type.STRING))
      {
         return new ValueToText(value);
      }

      return
         new ValueToText
         (
            Cast.build
            (
               value.get_origin(),
               Type.STRING,
               value,
               true
            )
         );
   }

   /**** Accessors ************************************************************/
   public ValueNode get_value ()
   {
      return value;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ValueToText ");
      sb.append(value.toString());
      sb.append(")");

      return sb.toString();
   }
}
