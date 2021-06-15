package tonkadur.fate.v1.lang.instruction;

import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.functional.Cons;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class SetFields extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final List<Cons<String, Computation>> field_assignments;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public SetFields
   (
      final Origin origin,
      final Computation target,
      final List<Cons<String, Computation>> field_assignments
   )
   {
      super(origin);

      this.target = target;
      this.field_assignments = field_assignments;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_set_fields(this);
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

      sb.append("(SetFields");
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
