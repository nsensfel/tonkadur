package tonkadur.wyrd.v1.lang.instruction;

import java.util.List;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class ExtraInstruction extends Instruction
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
   public ExtraInstruction
   (
      final String name,
      final List<Computation> parameters
   )
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
      iv.visit_extra_instruction(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(ExtraInstruction ");
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
