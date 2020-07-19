package tonkadur.fate.v1.lang.valued_node;

import java.util.List;
import java.util.Collections;

import tonkadur.functional.Cons;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ValueNode;

public class CondValue extends ValueNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Cons<ValueNode, ValueNode>> branches;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CondValue
   (
      final Origin origin,
      final Type return_type,
      final List<Cons<ValueNode, ValueNode>> branches
   )
   {
      super(origin, return_type);

      this.branches = branches;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static CondValue build
   (
      final Origin origin,
      final List<Cons<ValueNode, ValueNode>> branches
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type first_type;
      Type candidate_hint, hint;

      first_type = branches.get(0).get_cdr().get_type();
      hint = first_type;

      for (final Cons<ValueNode, ValueNode> entry: branches)
      {
         if (!entry.get_car().get_type().can_be_used_as(Type.BOOLEAN))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  entry.get_car().get_origin(),
                  entry.get_car().get_type(),
                  Collections.singleton(Type.BOOLEAN)
               )
            );
         }

         if (entry.get_cdr().get_type().equals(hint))
         {
            continue;
         }

         candidate_hint = entry.get_cdr().get_type().try_merging_with(hint);

         if (candidate_hint != null)
         {
            hint = candidate_hint;

            continue;
         }

         ErrorManager.handle
         (
            new ConflictingTypeException
            (
               entry.get_cdr().get_origin(),
               entry.get_cdr().get_type(),
               first_type
            )
         );

         hint = (Type) hint.generate_comparable_to(entry.get_cdr().get_type());

         if (hint.equals(Type.ANY))
         {
            ErrorManager.handle
            (
               new IncomparableTypeException
               (
                  entry.get_cdr().get_origin(),
                  entry.get_cdr().get_type(),
                  first_type
               )
            );
         }
      }

      return new CondValue(origin, hint, branches);
   }

   /**** Accessors ************************************************************/

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(CondValue");
      sb.append(System.lineSeparator());

      for (final Cons<ValueNode, ValueNode> entry: branches)
      {
         sb.append(System.lineSeparator());
         sb.append("Condition:");
         sb.append(System.lineSeparator());
         sb.append(entry.get_car().toString());
         sb.append(System.lineSeparator());
         sb.append("Value:");
         sb.append(entry.get_cdr().toString());
         sb.append(System.lineSeparator());
      }

      sb.append(")");

      return sb.toString();
   }
}
