package tonkadur.fate.v1.lang.computation;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class IfElseValue extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Computation if_true;
   protected final Computation if_false;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfElseValue
   (
      final Origin origin,
      final Type return_type,
      final Computation condition,
      final Computation if_true,
      final Computation if_false
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
      final Computation condition,
      final Computation if_true,
      final Computation if_false
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      Type hint;
      final Type if_true_type;
      final Type if_false_type;

      if (!condition.get_type().can_be_used_as(Type.BOOLEAN))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               condition.get_origin(),
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

      hint = if_true_type.try_merging_with(if_false_type);

      if (hint != null)
      {
         return new IfElseValue(origin, hint, condition, if_true, if_false);
      }

      ErrorManager.handle
      (
         new ConflictingTypeException
         (
            if_false.get_origin(),
            if_false_type,
            if_true_type
         )
      );

      hint =
         (Type) if_false_type.generate_comparable_to(if_true_type);

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException
            (
               if_false.get_origin(),
               if_false_type,
               if_true_type
            )
         );
      }

      return new IfElseValue(origin, hint, condition, if_true, if_false);
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_if_else_value(this);
   }

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
