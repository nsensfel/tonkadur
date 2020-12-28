package tonkadur.wyrd.v1.lang.instruction;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class AddEventOption extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddEventOption (final String name, final List<Computation> parameters)
   {
      this.name = name;
      this.parameters = parameters;
   }

   /**** Accessors ************************************************************/
   public String get_name ()
   {
      return name;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_add_event_option(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(AddEventOption ");
      sb.append(name);

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
