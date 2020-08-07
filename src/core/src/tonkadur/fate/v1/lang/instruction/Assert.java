package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.RichTextNode;
import tonkadur.fate.v1.lang.meta.Computation;

public class Assert extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final RichTextNode message;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Assert
   (
      final Origin origin,
      final Computation condition,
      final RichTextNode message
   )
   {
      super(origin);

      this.condition = condition;
      this.message = message;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Assert build
   (
      final Origin origin,
      final Computation condition,
      final RichTextNode message
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

      return new Assert(origin, condition, message);
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

   public RichTextNode get_message ()
   {
      return message;
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
