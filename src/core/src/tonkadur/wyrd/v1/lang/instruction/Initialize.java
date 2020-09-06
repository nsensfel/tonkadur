package tonkadur.wyrd.v1.lang.instruction;

import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.meta.Instruction;
import tonkadur.wyrd.v1.lang.meta.InstructionVisitor;

public class Initialize extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Address address;
   protected final Type type;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public Initialize (final Address address, final Type type)
   {
      this.address = address;
      this.type = type;
   }

   /**** Accessors ************************************************************/
   public Address get_address ()
   {
      return address;
   }

   public Type get_type ()
   {
      return type;
   }

   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_initialize(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(Initialize ");
      sb.append(address.toString());
      sb.append(" ");
      sb.append(type.toString());
      sb.append(")");

      return sb.toString();
   }
}
