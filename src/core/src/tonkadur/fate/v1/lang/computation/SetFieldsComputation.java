package tonkadur.fate.v1.lang.computation;

import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.functional.Cons;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class SetFieldsComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final List<Cons<String, Computation>> field_assignments;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected SetFieldsComputation
   (
      final Origin origin,
      final Computation target,
      final List<Cons<String, Computation>> field_assignments
   )
   {
      super(origin, target.get_type());

      this.target = target;
      this.field_assignments = field_assignments;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public static SetFieldsComputation build
   (
      final Origin origin,
      final Computation target,
      final List<Cons<Origin, Cons<String, Computation>>> field_assignments
   )
   throws Throwable
   {
      // A bit of a lazy solution: build field references, then extract the data
      final List<Cons<String, Computation>> assignments;

      target.expect_non_string();

      assignments = new ArrayList<Cons<String, Computation>>();

      for
      (
         final Cons<Origin, Cons<String, Computation>> entry: field_assignments
      )
      {
         final FieldAccess fa;
         final Computation cp;

         fa =
            FieldAccess.build
            (
               entry.get_car(),
               target,
               entry.get_cdr().get_car()
            );

         cp = entry.get_cdr().get_cdr();

         RecurrentChecks.handle_expected_type_propagation(cp, fa.get_type());
         RecurrentChecks.assert_can_be_used_as(cp, fa.get_type());

         assignments.add(new Cons(fa.get_field_name(), cp));
      }

      return new SetFieldsComputation(origin, target, assignments);
   }

   /**** Constructors *********************************************************/

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_set_fields(this);
   }

   public Computation get_target ()
   {
      return target;
   }

   public List<Cons<String, Computation>> get_assignments ()
   {
      return field_assignments;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(SetFieldsComputation");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("element:");
      sb.append(System.lineSeparator());
      sb.append(target.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      for (final Cons<String, Computation> assignment: field_assignments)
      {
         sb.append(assignment.get_car());
         sb.append(": ");
         sb.append(assignment.get_cdr());
         sb.append(System.lineSeparator());

      }

      sb.append(")");

      return sb.toString();
   }
}
