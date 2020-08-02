package tonkadur.fate.v1.lang.computation;

import java.util.List;
import java.util.Collections;

import tonkadur.functional.Cons;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class SwitchValue extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final Computation default_value;
   protected final List<Cons<Computation, Computation>> branches;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SwitchValue
   (
      final Origin origin,
      final Type return_type,
      final Computation target,
      final List<Cons<Computation, Computation>> branches,
      final Computation default_value
   )
   {
      super(origin, return_type);

      this.target = target;
      this.branches = branches;
      this.default_value = default_value;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static SwitchValue build
   (
      final Origin origin,
      final Computation target,
      final List<Cons<Computation, Computation>> branches,
      final Computation default_value
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type target_type;
      final Type first_type;
      Type candidate_hint, hint;

      target_type = target.get_type();
      first_type = branches.get(0).get_cdr().get_type();
      hint = first_type;

      for (final Cons<Computation, Computation> entry: branches)
      {
         if (!entry.get_car().get_type().can_be_used_as(target_type))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  entry.get_car().get_origin(),
                  entry.get_car().get_type(),
                  Collections.singleton(target_type)
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

      if (default_value.get_type().equals(hint))
      {
         return new SwitchValue(origin, hint, target, branches, default_value);
      }

      candidate_hint = default_value.get_type().try_merging_with(hint);

      if (candidate_hint != null)
      {
         hint = candidate_hint;

         return new SwitchValue(origin, hint, target, branches, default_value);
      }

      ErrorManager.handle
      (
         new ConflictingTypeException
         (
            default_value.get_origin(),
            default_value.get_type(),
            first_type
         )
      );

      hint = (Type) hint.generate_comparable_to(default_value.get_type());

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException
            (
               default_value.get_origin(),
               default_value.get_type(),
               first_type
            )
         );
      }

      return new SwitchValue(origin, hint, target, branches, default_value);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_switch_value(this);
   }

   public List<Cons<Computation, Computation>> get_branches ()
   {
      return branches;
   }

   public Computation get_target ()
   {
      return target;
   }

   public Computation get_default ()
   {
      return default_value;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(SwitchValue");
      sb.append(System.lineSeparator());

      for (final Cons<Computation, Computation> entry: branches)
      {
         sb.append(System.lineSeparator());
         sb.append("On value:");
         sb.append(System.lineSeparator());
         sb.append(entry.get_car().toString());
         sb.append(System.lineSeparator());
         sb.append("Return:");
         sb.append(entry.get_cdr().toString());
         sb.append(System.lineSeparator());
      }

      sb.append(System.lineSeparator());
      sb.append("default:");
      sb.append(default_value.toString());
      sb.append(System.lineSeparator());

      sb.append(")");

      return sb.toString();
   }
}
