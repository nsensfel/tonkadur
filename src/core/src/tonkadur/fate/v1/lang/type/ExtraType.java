package tonkadur.fate.v1.lang.type;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeArityException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class ExtraType extends Type
{
   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public ExtraType
   (
      final Origin origin,
      final Type parent,
      final String name,
      final List<Type> parameters
   )
   {
      super(origin, parent, name, parameters);
   }

   @Override
   public Type generate_variant
   (
      final Origin origin,
      final List<Type> parameters
   )
   throws Throwable
   {
      if (this.parameters.size() != parameters.size())
      {
         ErrorManager.handle(new InvalidTypeArityException(origin, this));
      }

      return new ExtraType(origin, this, name, parameters);
   }


   /**** Accessors ************************************************************/

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof FutureType)
      {
         return can_be_used_as(((FutureType) t).get_resolved_type());
      }
      else if (t instanceof ExtraType)
      {
         final ExtraType e;

         e = (ExtraType) t;

         if (get_base_type().get_name().equals(e.get_base_type().get_name()))
         {
            try
            {
               RecurrentChecks.assert_types_matches_signature
               (
                  Origin.BASE_LANGUAGE,
                  parameters,
                  e.parameters
               );
            }
            catch (final Throwable _e)
            {
               return false;
            }

            return true;
         }

      }

      return false;
   }

   /*
    * This is for the very special case where a type is used despite not being
    * even a sub-type of the expected one. Using this rather expensive function,
    * the most restrictive shared type will be returned. If no such type exists,
    * the ANY time is returned.
    */
   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      return Type.ANY;
   }

   @Override
   public Type get_act_as_type ()
   {
      return get_base_type();
   }

   public List<Type> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new ExtraType(origin, this, name, parameters);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("ExtraType::");
      sb.append(name);

      for (final Type t: parameters)
      {
         sb.append("<");
         sb.append(t.toString());
         sb.append(">");
      }

      return sb.toString();
   }
}
