package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Free extends GenericInstruction
{
   public static Collection<String> get_aliases ()
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("free");
      aliases.add("delete");
      aliases.add("destroy");

      return aliases;
   }

   public static Instruction build
   (
      final Origin origin,
      final String alias_,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation target;
      final Type target_type;

      if (call_parameters.size() != 1)
      {
         // TODO: Error.
         System.err.print
         (
            "[E] Wrong number of arguments at "
            + origin.toString()
         );
      }

      call_parameters.get(0).expect_non_string();

      target = call_parameters.get(0);

      target_type = target.get_type();

      if (target_type instanceof PointerType)
      {
         return new Free(origin, target);
      }

      ErrorManager.handle
      (
         new InvalidTypeException
         (
            origin,
            target_type,
            Collections.singletonList
            (
               new PointerType(origin, Type.ANY, "Any Pointer")
            )
         )
      );

      return new Free(origin, target);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation value_reference;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Free (final Origin origin, final Computation value_reference)
   {
      super(origin);

      this.value_reference = value_reference;
   }

   /**** Accessors ************************************************************/
   public Computation get_reference ()
   {
      return value_reference;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Free ");
      sb.append(value_reference.toString());

      sb.append(")");

      return sb.toString();
   }
}
