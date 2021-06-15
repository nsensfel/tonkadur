package tonkadur.fate.v1.lang.computation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Origin;

import tonkadur.functional.Cons;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class GenericComputation extends Computation
{
   /***************************************************************************/
   /**** STATIC ***************************************************************/
   /***************************************************************************/
   protected static final Map<String, Cons<GenericComputation, Object>>
      REGISTERED;

   static
   {
      REGISTERED = new HashMap<String, Cons<GenericComputation, Object>>();
   }

   public static GenericComputation build
   (
      final Origin origin,
      final String name,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Cons<GenericComputation, Object> target;

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
   protected GenericComputation
   (
      final Origin origin,
      final Type type,
      final String name
   )
   {
      super(origin, type);

      this.name = name;
   }

   protected GenericComputation build
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
            "Missing build function for GenericComputation '"
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
            "There already is a GenericComputation with the name '"
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
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_generic_computation(this);
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

      sb.append("(GenericInstruction (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(get_name());
      sb.append(")");

      return sb.toString();
   }
}
