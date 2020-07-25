package tonkadur.fate.v1.lang.meta;

import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Computation;

public class ExtensionComputation extends Computation
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ExtensionComputation
   (
      final Origin origin,
      final Type result_type
   )
   {
      super(origin, result_type);
   }

   public ExtensionComputation build
   (
      final World world,
      final Context context,
      final Origin origin,
      final List<Computation> parameters
   )
   {
      return new ExtensionComputation(Origin.BASE_LANGUAGE, Type.ANY);
   }
}
