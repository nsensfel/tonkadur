package tonkadur.fate.v1.lang.valued_node;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ValueNode;

public class Cast extends ValueNode
{
   protected static final Map<Type,Set<Type>> allowed_type_changes;

   static
   {
      allowed_type_changes = new HashMap<Type,Set<Type>>();

      allowed_type_changes.put
      (
         Type.INT,
         Type.NUMBER_TYPES
      );

      allowed_type_changes.put
      (
         Type.FLOAT,
         Type.NUMBER_TYPES
      );

      allowed_type_changes.put
      (
         Type.DICT,
         Collections.emptySet()
      );

      allowed_type_changes.put
      (
         Type.SET,
         Collections.emptySet()
      );

      allowed_type_changes.put
      (
         Type.LIST,
         Collections.emptySet()
      );

      allowed_type_changes.put
      (
         Type.BOOLEAN,
         Collections.emptySet()
      );

      allowed_type_changes.put
      (
         Type.ANY,
         Collections.singleton(Type.ANY)
      );

      allowed_type_changes.put
      (
         Type.STRING,
         Type.SIMPLE_BASE_TYPES
      );
   }
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode value;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Cast
   (
      final Origin origin,
      final Type to,
      final ValueNode value
   )
   {
      super(origin, to);
      this.value = value;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Cast build
   (
      final Origin origin,
      final Type to,
      final ValueNode value
   )
   throws
      IncompatibleTypeException,
      IncomparableTypeException
   {
      final Type hint;

      if
      (
         (value.get_type().can_be_used_as(to))
         ||
         (
            (to.is_base_type())
            &&
            (
               allowed_type_changes.get(to).contains
               (
                  value.get_type().get_base_type()
               )
            )
         )
      )
      {
         return new Cast(origin, to, value);
      }

      hint = (Type) value.get_type().generate_comparable_to(to);

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncompatibleTypeException(origin, value.get_type(), to)
         );

         ErrorManager.handle
         (
            new IncomparableTypeException(origin, value.get_type(), to)
         );
      }
      else
      {
         ErrorManager.handle
         (
            new IncompatibleTypeException(origin, value.get_type(), to, hint)
         );
      }

      return new Cast(origin, hint, value);
   }

   /**** Accessors ************************************************************/

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(Cast (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(value.toString());
      sb.append(")");

      return sb.toString();
   }
}
