package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.functional.Cons;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.Computation;

public class SetFieldsComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference target;
   protected final List<Cons<String, Computation>> field_assignments;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetFieldsComputation
   (
      final Origin origin,
      final Reference target,
      final List<Cons<String, Computation>> field_assignments
   )
   {
      super(origin, target.get_type());

      this.target = target;
      this.field_assignments = field_assignments;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_set_fields(this);
   }

   public Reference get_target ()
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
