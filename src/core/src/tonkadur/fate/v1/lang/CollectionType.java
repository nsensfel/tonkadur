package tonkadur.fate.v1.lang;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class CollectionType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type content_type;
   protected final boolean is_set;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public static CollectionType build
   (
      final Origin origin,
      final Type content_type,
      final boolean is_set,
      final String name
   )
   throws InvalidTypeException
   {
      if
      (
         !Type.COLLECTION_COMPATIBLE_TYPES.contains
         (
            content_type.get_true_type()
         )
      )
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               content_type,
               Type.COLLECTION_COMPATIBLE_TYPES
            )
         );
      }

      return new CollectionType(origin, content_type, is_set, name);
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
      if (t instanceof CollectionType)
      {
         final CollectionType ct;

         ct = (CollectionType) t;

         return
            (
               (!ct.is_set || is_set)
               && content_type.can_be_used_as(ct.content_type)
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
      final CollectionType ct;

      if (!(de instanceof CollectionType))
      {
         return Type.ANY;
      }

      ct = (CollectionType) de;

      return
         new CollectionType
         (
            get_origin(),
            ((Type) content_type.generate_comparable_to (ct.content_type)),
            (ct.is_set || is_set),
            name
         );
   }


   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (is_set)
      {
         sb.append("(Set ");
      }
      else
      {
         sb.append("(List ");
      }

      sb.append(content_type.toString());
      sb.append(")::");
      sb.append(name);

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   protected CollectionType
   (
      final Origin origin,
      final Type content_type,
      final boolean is_set,
      final String name
   )
   {
      super(origin, (is_set ? Type.SET : Type.LIST), name);

      this.is_set = is_set;
      this.content_type = content_type;
   }
}