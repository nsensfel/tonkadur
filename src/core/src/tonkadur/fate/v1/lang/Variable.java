package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.List;

import tonkadur.functional.Merge;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

import tonkadur.fate.v1.lang.type.Type;

public class Variable extends DeclaredEntity
{
   protected static final Variable ANY;

   static
   {
      ANY =
         new Variable
         (
            Origin.BASE_LANGUAGE,
            VariableScope.ANY,
            Type.ANY,
            /*
             * Use of a space necessary to avoid conflicting with a user created
             * type.
             */
            "undetermined variable"
         );
   }

   public static Variable value_on_missing ()
   {
      return ANY;
   }

   @Override
   public /* static */ String get_type_name ()
   {
      return "Variable";
   }


   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final VariableScope scope;
   protected final Type type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Variable
   (
      final Origin origin,
      final VariableScope scope,
      final Type type,
      final String name
   )
   {
      super(origin, name);

      this.scope = scope;
      this.type = type;
   }

   /**** Accessors ************************************************************/
   public Type get_type ()
   {
      return type;
   }

   public VariableScope get_scope ()
   {
      return scope;
   }

   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      final VariableScope new_scope;
      final Type new_type;
      final Variable v;

      if (!(de instanceof Variable))
      {
         return ANY;
      }

      v = (Variable) de;

      new_scope = scope.generate_compatible_with(v.scope);
      new_type = (Type) type.generate_comparable_to(v.type);

      return new Variable(origin, new_scope, new_type, name);
   }

   /**** Misc. ****************************************************************/
   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      if (de instanceof Variable)
      {
         final Variable v;

         v = (Variable) de;

         return
            (
               (!scope.equals(v.scope))
               || !type.can_be_used_as(v.type)
            );
      }

      return true;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(");
      sb.append(scope.toString());
      sb.append(" ");
      sb.append(type.get_name());
      sb.append(" Variable ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
}
