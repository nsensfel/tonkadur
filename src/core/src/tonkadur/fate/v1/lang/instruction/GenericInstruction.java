package tonkadur.fate.v1.lang.instruction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Origin;

import tonkadur.functional.Cons;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class GenericInstruction extends Instruction
{
   /***************************************************************************/
   /**** STATIC ***************************************************************/
   /***************************************************************************/
   protected static final Map<String, Cons<GenericInstruction, Object>>
      REGISTERED;

   static
   {
      REGISTERED = new HashMap<String, Cons<GenericInstruction, Object>>();
   }

   public static GenericInstruction build
   (
      final Origin origin,
      final String name,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Cons<GenericInstruction, Object> target;

      target = REGISTERED.get(name);

      if (target == null)
      {
         // TODO Exception handling.
      }

      return target.get_car().build(origin, call_parameters, target.get_cdr());
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected GenericInstruction
   (
      final Origin origin,
      final String name
   )
   {
      super(origin);

      this.name = name;
   }

   protected GenericInstruction build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object constructor_parameter
   )
   throws Throwable
   {
      throw
         new Exception
         (
            "Missing build function for GenericInstruction '"
            + name
            + "'."
         );
   }

   protected void register
   (
      final String name,
      final Object constructor_parameter
   )
   throws Exception
   {
      if (REGISTERED.containsKey(name))
      {
         // TODO Exception handling.
         new Exception
         (
            "There already is a GenericInstruction with the name '"
            + name
            + "'."
         );

         return;
      }

      REGISTERED.put(name, new Cons(this, constructor_parameter));
   }

   protected void register (final Object constructor_parameter)
   throws Exception
   {
      register(get_name(), constructor_parameter);
   }

   protected void register
   (
      final Collection<String> names,
      final Object constructor_parameter
   )
   throws Exception
   {
      for (final String name: names)
      {
         register(name, constructor_parameter);
      }
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor cv)
   throws Throwable
   {
      cv.visit_generic_instruction(this);
   }

   public String get_name ()
   {
      return name;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(GenericInstruction ");
      sb.append(get_name());
      sb.append(")");

      return sb.toString();
   }
}
