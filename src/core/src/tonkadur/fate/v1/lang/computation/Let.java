package tonkadur.fate.v1.lang.computation;

import java.util.List;

import tonkadur.functional.Cons;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.type.Type;

public class Let extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation computation;
   protected final List<Cons<Variable, Computation>> assignments;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Let
   (
      final Origin origin,
      final List<Cons<Variable, Computation>> assignments,
      final Computation computation
   )
   {
      super(origin, computation.get_type());

      this.computation = computation;
      this.assignments = assignments;
   }

   /**** Accessors ************************************************************/
   @Override
   public void expect_non_string ()
   throws ParsingError
   {
      computation.expect_non_string();
   }

   @Override
   public void expect_string ()
   throws ParsingError
   {
      computation.expect_string();
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_let(this);
   }

   public Computation get_computation ()
   {
      return computation;
   }

   public List<Cons<Variable, Computation>> get_assignments ()
   {
      return assignments;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Let (");

      for (final Cons<Variable, Computation> assignment: assignments)
      {
         sb.append("(");
         sb.append(assignment.get_car().get_type());
         sb.append(" ");
         sb.append(assignment.get_car().get_name());
         sb.append(" ");
         sb.append(assignment.get_cdr().toString());
         sb.append(")");
      }
      sb.append(") ");

      sb.append(computation.toString());

      sb.append(")");

      return sb.toString();
   }
}
