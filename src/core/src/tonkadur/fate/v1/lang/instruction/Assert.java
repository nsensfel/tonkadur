package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class Assert extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Assert
   (
      final Origin origin,
      final Computation condition
   )
   {
      super(origin);

      this.condition = condition;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Assert build
   (
      final Origin origin,
      final Computation condition
   )
   throws InvalidTypeException
   {
      if (!condition.get_type().get_base_type().equals(Type.BOOLEAN))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               condition.get_origin(),
               condition.get_type(),
               Collections.singleton(Type.BOOLEAN)
            )
         );
      }

      return new Assert(origin, condition);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_assert(this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Assert");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      sb.append(")");

      return sb.toString();
   }
}
