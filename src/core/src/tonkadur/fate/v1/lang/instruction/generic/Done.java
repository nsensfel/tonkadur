package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Done extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("done");

      return aliases;
   }

   public static Instruction build
   (
      final Origin origin,
      final String _alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      if (call_parameters.size() != 0)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      return new Done(origin);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Done (final Origin origin)
   {
      super(origin);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      return "(Done)";
   }
}
