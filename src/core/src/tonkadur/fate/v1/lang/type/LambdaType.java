package tonkadur.fate.v1.lang.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class LambdaType extends Type
{
   public static final LambdaType ARCHETYPE;

   static
   {
      ARCHETYPE =
         new LambdaType
         (
            Origin.BASE_LANGUAGE,
            Type.ANY,
            "lambda",
            new ArrayList<Type>()
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type return_type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public LambdaType
   (
      final Origin origin,
      final Type return_type,
      final String name,
      final List<Type> signature
   )
   {
      super(origin, null, name, signature);

      this.return_type = return_type;
   }

   /**** Accessors ************************************************************/
   public Type get_return_type ()
   {
      return return_type;
   }

   public List<Type> get_signature ()
   {
      return parameters;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof LambdaType)
      {
         final Iterator<Type> i0, i1;
         final LambdaType lt;

         lt = (LambdaType) t;

         if
         (
            parameters.size() != lt.parameters.size()
            || !return_type.can_be_used_as(lt.return_type)
         )
         {
            return false;
         }

         i0 = parameters.iterator();
         i1 = lt.parameters.iterator();

         while(i0.hasNext())
         {
            if (!i0.next().can_be_used_as(i1.next()))
            {
               return false;
            }
         }

         return true;
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
      final Iterator<Type> i0, i1;
      final List<Type> resulting_signature;
      final Type resulting_return_type;
      final LambdaType lt;

      if (!(de instanceof LambdaType))
      {
         return Type.ANY;
      }

      lt = (LambdaType) de;

      if (lt.parameters.size() != parameters.size())
      {
         return Type.ANY;
      }

      resulting_signature = new ArrayList<Type>();
      resulting_return_type =
         (Type) return_type.generate_comparable_to(lt.return_type);

      i0 = parameters.iterator();
      i1 = lt.parameters.iterator();

      while(i0.hasNext())
      {
         resulting_signature.add
         (
            (Type) i0.next().generate_comparable_to(i1.next())
         );
      }

      return
         new LambdaType
         (
            get_origin(),
            resulting_return_type,
            name,
            resulting_signature
         );
   }

   @Override
   public Type get_act_as_type ()
   {
      return get_base_type();
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new LambdaType(origin, return_type, name, parameters);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Lambda ");
      sb.append(return_type.toString());
      sb.append(" (");

      for (final Type t: parameters)
      {
         sb.append(t.get_name());
      }

      sb.append("))::");
      sb.append(name);

      return sb.toString();
   }
}
