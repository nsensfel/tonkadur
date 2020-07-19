package tonkadur.fate.v1.lang.meta;

import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ValueNode;

public class ExtensionValueNode extends ValueNode
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ExtensionValueNode
   (
      final Origin origin,
      final Type result_type
   )
   {
      super(origin, result_type);
   }

   public ExtensionValueNode build
   (
      final World world,
      final Context context,
      final Origin origin,
      final List<ValueNode> parameters
   )
   {
      return new ExtensionValueNode(Origin.BASE_LANGUAGE, Type.ANY);
   }
}
