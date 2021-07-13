package tonkadur.fate.v1.lang.instruction.generic;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Free extends GenericInstruction
{
   protected static final Free ARCHETYPE;

   static
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      ARCHETYPE =
         new Free
         (
            Origin.BASE_LANGUAGE,
            null
         );

      aliases.add("free");
      aliases.add("delete");
      aliases.add("destroy");

      try
      {
         ARCHETYPE.register(aliases, null);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }
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
      super(origin, "free");

      this.value_reference = value_reference;
   }

   @Override
   public GenericInstruction build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object _constructor_parameter
   )
   throws Throwable
   {
      final Computation target;
      final Type target_type;

      if (call_parameters.size() != 1)
      {
         // Error.
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
