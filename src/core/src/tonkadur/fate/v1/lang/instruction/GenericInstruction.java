package tonkadur.fate.v1.lang.instruction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Origin;

import tonkadur.functional.Cons;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;

public abstract class GenericInstruction extends Instruction
{
   /***************************************************************************/
   /**** STATIC ***************************************************************/
   /***************************************************************************/
   protected static final Map<String, Class> REGISTERED;

   static
   {
      REGISTERED = new HashMap<String, Class>();
   }

   public static void register (final Class c)
   {
      try
      {
         for
         (
            final String alias:
               (Collection<String>) c.getMethod("get_aliases").invoke
               (
                  /* object = */null
               )
         )
         {
            final Class previous_entry;

            previous_entry = REGISTERED.get(alias);

            if (previous_entry != null)
            {
               // TODO Exception handling.
               throw new Exception
               (
                  "[F] Unable to add alias for Generic Fate Instruction '"
                  + alias
                  + "' from class '"
                  + c.getName()
                  + "': it has already been claimed by class '"
                  + previous_entry.getName()
                  + "'."
               );
            }

            REGISTERED.put(alias, c);
         }
      }
      catch (final Throwable t)
      {
         System.err.println
         (
            "Could not register Generic Fate Instruction class '"
            + c.getName()
            + "': "
         );

         t.printStackTrace();
      }
   }

   public static Instruction build
   (
      final Origin origin,
      final String name,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Class computation_class;

      computation_class = REGISTERED.get(name);

      if (computation_class == null)
      {
         // TODO use a separate class for this.
         throw
            new Exception
            (
               "[E] Unknown Generic Fate Instruction '"
               + name
               + "' at "
               + origin
               + "."
            );
      }

      System.out.println("Resolving GenericInstruction " + name + "...");

      return
         (Instruction) computation_class.getDeclaredMethod
         (
            "build",
            Origin.class,
            String.class,
            List.class
         ).invoke(/* object = */ null, origin, name, call_parameters);
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected GenericInstruction (final Origin origin)
   {
      super(origin);
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor cv)
   throws Throwable
   {
      cv.visit_generic_instruction(this);
   }
}
