package tonkadur.fate.v1.lang;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ValueNode;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

public class IfElseValue extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode condition;
   protected final ValueNode if_true;
   protected final ValueNode if_false;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfElseValue
   (
      final Origin origin,
      final Type return_type,
      final ValueNode condition,
      final ValueNode if_true,
      final ValueNode if_false
   )
   {
      super(origin, return_type);

      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfElseValue build
   (
      final Origin origin,
      final ValueNode condition,
      final ValueNode if_true,
      final ValueNode if_false
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type hint;
      final Type if_true_type;
      final Type if_false_type;

      if (!condition.get_type().can_be_used_as(Type.BOOLEAN))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               condition.get_type(),
               Collections.singleton(Type.BOOLEAN)
            )
         );
      }

      if_true_type = if_true.get_type();
      if_false_type = if_false.get_type();

      if (if_true_type.equals(if_false_type))
      {
         return
            new IfElseValue(origin, if_true_type, condition, if_true, if_false);
      }

      ErrorManager.handle
      (
         new ConflictingTypeException(origin, if_false_type, if_true_type)
      );

      hint =
         (Type) if_false_type.generate_comparable_to(if_true_type);

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException(origin, if_false_type, if_true_type)
         );
      }

      return new IfElseValue(origin, hint, condition, if_true, if_false);
   }

   /**** Accessors ************************************************************/

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(IfElseValue");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Condition:");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("If true:");
      sb.append(System.lineSeparator());
      sb.append(if_true.toString());

      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("If false:");
      sb.append(System.lineSeparator());
      sb.append(if_false.toString());

      sb.append(")");

      return sb.toString();
   }
}
