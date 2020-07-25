package tonkadur.fate.v1.lang.valued_node;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.RichTextNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class ValueToRichText extends RichTextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode value;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ValueToRichText (final ValueNode value)
   {
      super(value.get_origin());

      this.value = value;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ValueToRichText build (final ValueNode value)
   throws
      IncompatibleTypeException,
      IncomparableTypeException
   {
      final Type value_base_type;

      value_base_type = value.get_type().get_base_type();

      if (value_base_type.equals(Type.STRING))
      {
         return new ValueToRichText(value);
      }

      return
         new ValueToRichText
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
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_value_to_rich_text(this);
   }

   public ValueNode get_value ()
   {
      return value;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ValueToRichText ");
      sb.append(value.toString());
      sb.append(")");

      return sb.toString();
   }
}
