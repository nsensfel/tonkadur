package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.functional.Cons;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class CondValue extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Cons<Computation, Computation>> branches;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected CondValue
   (
      final Origin origin,
      final Type return_type,
      final List<Cons<Computation, Computation>> branches
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
      final List<Cons<Computation, Computation>> branches
   )
   throws ParsingError
   {
      Type hint;

      hint = null;

      for (final Cons<Computation, Computation> entry: branches)
      {
         if (entry.get_cdr().get_type().can_be_used_as(Type.STRING))
         {
            expect_string = 1;

            hint = Type.STRING;

            break;
         }
         else if (entry.get_cdr().get_type() != Type.ANY)
         {
            expect_string = 0;

            hint = entry.get_cdr().get_type();

            break;
         }
      }

      for (final Cons<Computation, Computation> entry: branches)
      {
         entry.get_car().expect_non_string();

         RecurrentChecks.assert_can_be_used_as(entry.get_car(), Type.BOOL);

         if (hint != null)
         {
            hint = RecurrentChecks.assert_can_be_used_as(entry.get_cdr(), hint);
         }
      }

      if (hint == null)
      {
         hint = new FutureType(origin, new ArrayList<Type>());
      }

      return new CondValue(origin, hint, branches);
   }

   /**** Accessors ************************************************************/
   @Override
   public void expect_non_string ()
   {
      if (get_type() instanceof FutureType)
      {
         Type hint;

         hint = branches.get(0).get_cdr().expect_non_string();

         for (final Cons<Computation, Computation> entry: branches)
         {
            entry.get_cdr().expect_non_string();

            hint = RecurrentChecks.assert_can_be_used_as(entry.get_cdr(), hint);
         }

         ((FutureType) get_type()).resolve_to(hint);
      }
   }

   @Override
   public void expect_string ()
   {
      if (get_type() instanceof FutureType)
      {
         Type hint;

         hint = branches.get(0).get_cdr().expect_string();

         for (final Cons<Computation, Computation> entry: branches)
         {
            entry.get_cdr().expect_non_string();

            hint = RecurrentChecks.assert_can_be_used_as(entry.get_cdr(), hint);
         }

         ((FutureType) get_type()).resolve_to(hint);
      }
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_cond_value(this);
   }

   public List<Cons<Computation, Computation>> get_branches ()
   {
      return branches;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(CondValue");
      sb.append(System.lineSeparator());

      for (final Cons<Computation, Computation> entry: branches)
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
