package tonkadur.fate.v1.lang.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;
import tonkadur.fate.v1.lang.meta.Computation;

public class SequenceType extends Type
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Type> signature;
   protected boolean signature_is_defined;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public SequenceType
   (
      final Origin origin,
      final String name,
      final List<Type> signature
   )
   {
      super(origin, null, name);

      this.signature = signature;
      this.signature_is_defined = true;
   }

   public SequenceType
   (
      final Origin origin,
      final String name
   )
   {
      super(origin, null, name);

      this.signature = new ArrayList<Type>();
      this.signature_is_defined = false;
   }

   /**** Accessors ************************************************************/
   public List<Type> get_signature ()
   {
      return signature;
   }

   public void propose_signature (final List<Type> signature)
   {
      if (signature_is_defined)
      {
         return;
      }

      this.signature.addAll(signature);

      signature_is_defined = true;
   }

   public void propose_signature_from_parameters
   (
      final List<Computation> params
   )
   {
      if (signature_is_defined)
      {
         return;
      }

      for (final Computation c: params)
      {
         signature.add(c.get_type());
      }

      signature_is_defined = true;
   }

   /**** Compatibility ********************************************************/
   @Override
   public boolean can_be_used_as (final Type t)
   {
      if (t instanceof SequenceType)
      {
         final Iterator<Type> i0, i1;
         final SequenceType lt;

         lt = (SequenceType) t;

         propose_signature(lt.get_signature());

         if (signature.size() != lt.signature.size())
         {
            return false;
         }

         i0 = signature.iterator();
         i1 = lt.signature.iterator();

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
      final SequenceType lt;

      if (!(de instanceof SequenceType))
      {
         return Type.ANY;
      }

      lt = (SequenceType) de;

      if (lt.signature.size() != signature.size())
      {
         return Type.ANY;
      }

      resulting_signature = new ArrayList<Type>();

      i0 = signature.iterator();
      i1 = lt.signature.iterator();

      while(i0.hasNext())
      {
         resulting_signature.add
         (
            (Type) i0.next().generate_comparable_to(i1.next())
         );
      }

      return new SequenceType(get_origin(), name, resulting_signature);
   }

   @Override
   public Type get_act_as_type ()
   {
      return Type.SEQUENCE;
   }

   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      return new SequenceType(origin, name, signature);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Sequence (");

      for (final Type t: signature)
      {
         sb.append(t.get_name());
         sb.append(" ");
      }

      sb.append("))::");
      sb.append(name);

      return sb.toString();
   }
}
