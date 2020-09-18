package tonkadur.fate.v1.lang.instruction;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Reference;

public class Free extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference value_reference;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Free
   (
      final Origin origin,
      final Reference value_reference
   )
   {
      super(origin);

      this.value_reference = value_reference;
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_free(this);
   }

   public Reference get_reference ()
   {
      return value_reference;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Free ");
      sb.append(value_reference.toString());

      sb.append(")");

      return sb.toString();
   }
}
