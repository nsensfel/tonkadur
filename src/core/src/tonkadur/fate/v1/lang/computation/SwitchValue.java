package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.functional.Cons;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

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
   throws ParsingError
   {
      final Type target_type;
      Type candidate_hint;

      target_type = target.get_type();
      target_type.expect_string();

      candidate_hint = branches.get(0).get_car().get_type();

      for (final Cons<Computation, Computation> entry: branches)
      {
         entry.get_car().expect_string();

         candidate_hint =
            RecurrentChecks.assert_can_be_used_as
            (
               entry.get_car(),
               candidate_hint
            );
      }

      return
         new SwitchValue
         (
            origin,
            new FutureType(origin),
            target,
            branches,
            default_value
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void expect_non_string ()
   throws ParsingError
   {
      Type hint;

      default_value.expect_non_string();

      hint = default_value.get_type();

      for (final Cons<Computation, Computation> entry: branches)
      {
         entry.get_cdr().expect_non_string();

         hint = RecurrentChecks.assert_can_be_used_as(entry.get_cdr(), hint);
      }

      ((FutureType) get_type()).resolve_to(hint);
   }

   @Override
   public void expect_string ()
   throws ParsingError
   {
      Type hint;

      default_value.expect_string();

      hint = default_value.get_type();

      for (final Cons<Computation, Computation> entry: branches)
      {
         entry.get_cdr().expect_string();

         hint = RecurrentChecks.assert_can_be_used_as(entry.get_cdr(), hint);
      }

      ((FutureType) get_type()).resolve_to(hint);
   }

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
