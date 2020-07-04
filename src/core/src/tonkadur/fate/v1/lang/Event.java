package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.List;

import tonkadur.functional.Merge;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class Event extends DeclaredEntity
{
   protected static final Event ANY;

   static
   {
      ANY =
         new Event
         (
            new Origin(new Context(""), Location.BASE_LANGUAGE),
            new ArrayList<Type>(),
            /*
             * Use of a space necessary to avoid conflicting with a user created
             * type.
             */
            "undetermined event"
         );
   }

   public static Event value_on_missing ()
   {
      return ANY;
   }

   @Override
   public /* static */ String get_type_name ()
   {
      return "Event";
   }


   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Type> signature;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Event
   (
      final Origin origin,
      final List<Type> signature,
      final String name
   )
   {
      super(origin, name);

      this.signature = signature;
   }

   /**** Accessors ************************************************************/
   public List<Type> get_signature ()
   {
      return signature;
   }

   /**** Misc. ****************************************************************/
   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      if (de instanceof Event)
      {
         final Event e;

         e = (Event) de;

         if (signature.size() == e.signature.size())
         {
            final List<Boolean> compatibility_result;

            /*
             * Basically, the events are compatible if, and only if, the old
             * signature is as least as restrictive as the new one.
             */
            compatibility_result =
               (
                  new Merge<Type,Type,Boolean>()
                  {
                     @Override
                     protected Boolean merge_fun (final Type a, final Type b)
                     {
                        return
                           new Boolean(a.is_incompatible_with_declaration(b));
                     }
                  }.merge(signature, e.signature)
               );

            return compatibility_result.contains(Boolean.TRUE);
         }
      }

      return true;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(name);

      if (!signature.isEmpty())
      {
         boolean first_argument;

         sb.append(": ");

         first_argument = true;

         for (final Type type: signature)
         {
            if (first_argument)
            {
               first_argument = false;
            }
            else
            {
               sb.append(" -> ");
            }

            sb.append(type.get_name());
         }
      }

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
}
