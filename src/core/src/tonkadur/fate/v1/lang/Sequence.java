package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.Type;

public class Sequence extends DeclaredEntity
{
   @Override
   public /* static */ String get_type_name ()
   {
      return "Sequence";
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Instruction root;
   protected final List<Variable> signature;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Sequence
   (
      final Origin origin,
      final Instruction root,
      final String name,
      final List<Variable> signature
   )
   {
      super(origin, name);

      this.root = root;
      this.signature = signature;
   }

   /**** Accessors ************************************************************/
   public Instruction get_root ()
   {
      return root;
   }

   public List<Variable> get_signature ()
   {
      return signature;
   }

   /**** Compatibility ********************************************************/

   /*
    * This is for the very special case where a type is used despite not being
    * even a sub-type of the expected one. Using this rather expensive function,
    * the most restrictive shared type will be returned. If no such type exists,
    * the ANY time is returned.
    */
   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      return null;
   }

   public boolean assert_can_take_parameters
   (
      final Origin origin,
      final List<Computation> parameters
   )
   throws Throwable
   {
      final Iterator<Computation> param_it;
      final Iterator<Variable> sign_it;
      boolean result;

      if (signature.size() != parameters.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               origin,
               parameters.size(),
               signature.size(),
               signature.size()
            )
         );

         return false;
      }

      result = true;
      param_it = parameters.iterator();
      sign_it = signature.iterator();

      while (param_it.hasNext())
      {
         final Type param_type, sign_type;

         param_type = param_it.next().get_type();
         sign_type = sign_it.next().get_type();

         if (param_type.can_be_used_as(sign_type))
         {
            continue;
         }

         ErrorManager.handle
         (
            new IncompatibleTypeException(origin, param_type, sign_type)
         );

         if (param_type.generate_comparable_to(sign_type).equals(Type.ANY))
         {
            ErrorManager.handle
            (
               new IncomparableTypeException(origin, param_type, sign_type)
            );
         }
      }

      return true;
   }

   /**** Misc. ****************************************************************/
   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      return true;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Sequence ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
}
