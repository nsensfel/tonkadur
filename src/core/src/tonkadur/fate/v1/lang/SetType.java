package tonkadur.fate.v1.lang;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class SetType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type content_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public static SetType build
   (
      final Origin origin,
      final Type content_type,
      final String name
   )
   throws InvalidTypeException
   {
      if (!Type.SET_COMPATIBLE_TYPES.contains(content_type.get_true_type()))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               content_type,
               Type.SET_COMPATIBLE_TYPES
            )
         );
      }

      return new SetType(origin, content_type, name);
   }


   /**** Accessors ************************************************************/
   public Type get_content_type ()
   {
      return true_type;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof SetType)
      {
         return content_type.can_be_used_as(((SetType) t).content_type);
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
      if (!(de instanceof SetType))
      {
         return Type.ANY;
      }

      return
         new SetType
         (
            get_origin(),
            (
               (Type) content_type.generate_comparable_to
               (
                  ((SetType) de).content_type
               )
            ),
            name
         );
   }


   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Set ");
      sb.append(content_type.toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   protected SetType
   (
      final Origin origin,
      final Type content_type,
      final String name
   )
   {
      super(origin, Type.SET, name);

      this.content_type = content_type;
   }
}
