package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class DoWhile extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final List<Instruction> body;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected DoWhile
   (
      final Origin origin,
      final Computation condition,
      final List<Instruction> body
   )
   {
      super(origin);

      this.condition = condition;
      this.body = body;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static DoWhile build
   (
      final Origin origin,
      final Computation condition,
      final List<Instruction> body
   )
   throws InvalidTypeException
   {
      if (!condition.get_type().get_base_type().equals(Type.BOOL))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               condition.get_origin(),
               condition.get_type(),
               Collections.singleton(Type.BOOL)
            )
         );
      }

      return new DoWhile(origin, condition, body);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_do_while(this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public List<Instruction> get_body ()
   {
      return body;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(DoWhile");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      for (final Instruction i: body)
      {
         sb.append(System.lineSeparator());
         sb.append(i.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
