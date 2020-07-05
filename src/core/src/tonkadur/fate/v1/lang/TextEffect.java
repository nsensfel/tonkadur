package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.List;

import tonkadur.functional.Merge;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class TextEffect extends Event
{
   protected static final TextEffect ANY;

   static
   {
      ANY =
         new TextEffect
         (
            new Origin(new Context(""), Location.BASE_LANGUAGE),
            new ArrayList<Type>(),
            /*
             * Use of a space necessary to avoid conflicting with a user created
             * type.
             */
            "undetermined text_effect"
         );
   }

   public static TextEffect value_on_missing ()
   {
      return ANY;
   }

   @Override
   public /* static */ String get_type_name ()
   {
      return "TextEffect";
   }


   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public TextEffect
   (
      final Origin origin,
      final List<Type> signature,
      final String name
   )
   {
      super(origin, signature, name);
   }

   /**** Accessors ************************************************************/
   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      final List<Type> new_signature;
      final TextEffect e;

      if (!(de instanceof TextEffect))
      {
         return ANY;
      }

      e = (TextEffect) de;

      if (signature.size() != e.signature.size())
      {
         return ANY;
      }

      new_signature =
         new Merge<Type, Type, Type>()
         {
            @Override
            protected Type lambda (final Type a, final Type b)
            {
               return (Type) a.generate_comparable_to(b);
            }
         }.merge(signature, e.signature);

      return new TextEffect(origin, new_signature, name);
   }

   /**** Misc. ****************************************************************/
   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      if (de instanceof TextEffect)
      {
         final TextEffect e;

         e = (TextEffect) de;

         if (signature.size() == e.signature.size())
         {
            final List<Boolean> compatibility_result;

            /*
             * Basically, the events are compatible if, and only if, the old
             * signature is as least as restrictive as the new one.
             */
            compatibility_result =
               (
                  new Merge<Type, Type, Boolean>()
                  {
                     @Override
                     protected Boolean lambda (final Type a, final Type b)
                     {
                        return
                           new Boolean(a.can_be_used_as(b));
                     }
                  }.merge(signature, e.signature)
               );

            return compatibility_result.contains(Boolean.TRUE);
         }
      }

      return true;
   }
}
