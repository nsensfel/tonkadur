package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownStructureFieldException;


import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.computation.Default;
import tonkadur.fate.v1.lang.computation.GenericComputation;

public class AtReference extends GenericComputation
{
   protected static final AtReference ARCHETYPE;

   static
   {
      final List<String> aliases;

      ARCHETYPE =
         new AtReference
         (
            Origin.BASE_LANGUAGE,
            Type.INT,
            new Default
            (
               Origin.BASE_LANGUAGE,
               new PointerType
               (
                  Origin.BASE_LANGUAGE,
                  Type.INT,
                  "(ptr int)"
               )
            )
         );

      aliases = new ArrayList<String>();

      aliases.add("at");

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

   public static void initialize_class ()
   {
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected AtReference
   (
      final Origin origin,
      final Type reported_type,
      final Computation parent
   )
   {
      super(origin, reported_type, "at");

      this.parent = parent;
   }
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   @Override
   public GenericComputation build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object _registered_parameter
   )
   throws Throwable
   {
      Type current_type;

      if (call_parameters.size() != 1)
      {
         // TODO: Error.
      }

      call_parameters.get(0).expect_non_string();

      current_type = parent.get_type();

      if (!(current_type instanceof PointerType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singleton(Type.PTR)
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((PointerType) current_type).get_referenced_type();
      }

      return new AtReference(origin, current_type, parent);
   }

   /**** Accessors ************************************************************/
   public Computation get_parent ()
   {
      return parent;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AtReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent.toString());
      sb.append(")");

      return sb.toString();
   }
}
