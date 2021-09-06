package tonkadur.fate.v1.lang.type;

import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeArityException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class PointerType extends Type
{
   public static final PointerType ARCHETYPE;

   static
   {
      ARCHETYPE =
         new PointerType
         (
            Origin.BASE_LANGUAGE,
            Type.ANY,
            "ptr"
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public PointerType
   (
      final Origin origin,
      final Type referenced_type,
      final String name
   )
   {
      super
      (
         origin,
         ((referenced_type == Type.ANY) ? null : ARCHETYPE),
         name,
         Collections.singletonList(referenced_type)
      );
   }

   /**** Accessors ************************************************************/
   public Type get_referenced_type ()
   {
      final Type result;

      result = parameters.get(0);

      if (result instanceof FutureType)
      {
         return ((FutureType) result).get_resolved_type();
      }

      return result;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof FutureType)
      {
         return can_be_used_as(((FutureType) t).get_resolved_type());
      }
      else if (t instanceof PointerType)
      {
         final PointerType dt;

         dt = (PointerType) t;

         return get_referenced_type().can_be_used_as(dt.get_referenced_type());
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
      final Type resulting_referenced_type;
      final PointerType dt;

      if (!(de instanceof PointerType))
      {
         return Type.ANY;
      }

      dt = (PointerType) de;

      resulting_referenced_type =
         (Type) get_referenced_type().generate_comparable_to
         (
            dt.get_referenced_type()
         );

      return new PointerType(get_origin(), resulting_referenced_type, name);
   }

   @Override
   public Type get_act_as_type ()
   {
      return ARCHETYPE;
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new PointerType(origin, get_referenced_type(), name);
   }

   @Override
   public Type generate_variant
   (
      final Origin origin,
      final List<Type> parameters
   )
   throws Throwable
   {
      if (this.parameters.size() != 1)
      {
         ErrorManager.handle(new InvalidTypeArityException(origin, this));
      }

      return new PointerType(origin, parameters.get(0), "auto gen");
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Pointer to ");
      sb.append(get_referenced_type().toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }
}
