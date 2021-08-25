package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class CarCdr extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("car");
      aliases.add("cdr");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation parent;
      final boolean is_car;
      Type current_type;

      if (call_parameters.size() != 1)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      parent = call_parameters.get(0);

      parent.expect_non_string();

      is_car = alias.equals("car");

      current_type = parent.get_type();

      if (!(current_type instanceof ConsType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singletonList(ConsType.ARCHETYPE)
            )
         );

         current_type = Type.ANY;
      }
      else if (is_car)
      {
         current_type = ((ConsType) current_type).get_car_type();
      }
      else
      {
         current_type = ((ConsType) current_type).get_cdr_type();
      }

      return new CarCdr(origin, parent, is_car, current_type);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;
   protected final boolean is_car;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CarCdr
   (
      final Origin origin,
      final Computation parent,
      final boolean is_car,
      final Type type
   )
   {
      super(origin, type);

      this.parent = parent;
      this.is_car = is_car;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_parent ()
   {
      return parent;
   }

   public boolean is_car ()
   {
      return is_car;
   }

   @Override
   public void use_as_reference ()
   throws ParsingError
   {
      parent.use_as_reference();
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (is_car)
      {
         sb.append("(car (");
      }
      else
      {
         sb.append("(cdr (");
      }

      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent);
      sb.append(")");

      return sb.toString();
   }
}
