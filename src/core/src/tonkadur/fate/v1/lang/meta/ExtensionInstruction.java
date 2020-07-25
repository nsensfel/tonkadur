package tonkadur.fate.v1.lang.meta;

import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Instruction;

public class ExtensionInstruction extends Instruction
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
      final List<Instruction> parameters
   )
   {
      return new ExtensionInstruction(Origin.BASE_LANGUAGE);
   }
}
