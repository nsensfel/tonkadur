package tonkadur.fate.v1.lang.meta;

import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionNode;

public class ExtensionInstruction extends InstructionNode
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ExtensionInstruction (final Origin origin)
   {
      super(origin);
   }

   public ExtensionInstruction build
   (
      final World world,
      final Context context,
      final Origin origin,
      final List<InstructionNode> parameters
   )
   {
      return new ExtensionInstruction(Origin.BASE_LANGUAGE);
   }
}
