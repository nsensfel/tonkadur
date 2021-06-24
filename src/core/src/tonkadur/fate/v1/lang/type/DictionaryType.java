package tonkadur.fate.v1.lang.type;

import tonkadur.error.ErrorManager;
import tonkadur.parser.ParsingError;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class DictionaryType extends Type
{
   public static final DictionaryType ARCHETYPE;

   static
   {
      ARCHETYPE =
         new DictionaryType
         (
            Origin.BASE_LANGUAGE,
            Type.ANY,
            Type.ANY,
            "dict"
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type key_type;
   protected final Type value_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public static DictionaryType build
   (
      final Origin origin,
      final Type key_type,
      final Type value_type,
      final String name
   )
   throws InvalidTypeException
   {
      if (!Type.COMPARABLE_TYPES.contains(key_type.get_act_as_type()))
      {
         ErrorManager.handle
         (
            new InvalidTypeException(origin, key_type, Type.COMPARABLE_TYPES)
         );
      }

      return new DictionaryType(origin, key_type, value_type, name);
   }


   /**** Accessors ************************************************************/
   public Type get_key_type ()
   {
      return key_type;
   }

   public Type get_value_type ()
   {
      return value_type;
   }

   @Override
   public Type get_act_as_type ()
   {
      return ARCHETYPE;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof DictionaryType)
      {
         final DictionaryType ct;

         ct = (DictionaryType) t;

         return
            (
               key_type.can_be_used_as(ct.key_type)
               && value_type.can_be_used_as(ct.value_type)
            );
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
      final DictionaryType ct;

      if (!(de instanceof DictionaryType))
      {
         return Type.ANY;
      }

      ct = (DictionaryType) de;

      return
         new DictionaryType
         (
            get_origin(),
            ((Type) key_type.generate_comparable_to(ct.key_type)),
            ((Type) value_type.generate_comparable_to(ct.value_type)),
            name
         );
   }


   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new DictionaryType(origin, key_type, value_type, name);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Dict ");
      sb.append(key_type.toString());
      sb.append(" ");
      sb.append(value_type.toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   protected DictionaryType
   (
      final Origin origin,
      final Type key_type,
      final Type value_type,
      final String name
   )
   {
      super(origin, null, name);

      this.key_type = key_type;
      this.value_type = value_type;
   }
}
