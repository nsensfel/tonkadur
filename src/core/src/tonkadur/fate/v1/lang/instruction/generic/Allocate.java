package tonkadur.fate.v1.lang.instruction.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.instruction.GenericInstruction;

public class Allocate extends GenericInstruction
{
   protected static final Allocate ARCHETYPE;

   static
   {
      final List<String> aliases;

      aliases = new ArrayList<String>();

      ARCHETYPE =
         new Allocate
         (
            Origin.BASE_LANGUAGE,
            null,
            null
         );

      aliases.add("allocate");
      aliases.add("alloc");
      aliases.add("malloc");
      aliases.add("malloc");
      aliases.add("new");
      aliases.add("create");

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
   protected final Computation target;
   protected final Type allocated_type;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Allocate
   (
      final Origin origin,
      final Type allocated_type,
      final Computation target
   )
   {
      super(origin, "allocate");

      this.allocated_type = allocated_type;
      this.target = target;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   @Override
   public GenericInstruction build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object _constructor_parameter
   )
   throws Throwable
   {
      final Type target_type;

      if (call_parameters.size() != 1)
      {
         // Error.
      }

      call_parameters.get(0).expect_non_string();

      target_type = target.get_type();

      if (target_type instanceof PointerType)
      {
         return
            new Allocate
            (
               origin,
               ((PointerType) target_type).get_referenced_type(),
               target
            );
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

      return new Allocate(origin, Type.ANY, target);
   }

   /**** Accessors ************************************************************/
   public Computation get_target ()
   {
      return target;
   }

   public Type get_allocated_type ()
   {
      return allocated_type;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Allocate ");
      sb.append(target.toString());
      sb.append(")");

      return sb.toString();
   }
}
