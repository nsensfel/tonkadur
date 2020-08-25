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

public class For extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Instruction pre;
   protected final List<Instruction> body;
   protected final Instruction post;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected For
   (
      final Origin origin,
      final Computation condition,
      final Instruction pre,
      final List<Instruction> body,
      final Instruction post
   )
   {
      super(origin);

      this.condition = condition;
      this.pre = pre;
      this.body = body;
      this.post = post;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static For build
   (
      final Origin origin,
      final Computation condition,
      final Instruction pre,
      final List<Instruction> body,
      final Instruction post
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

      return new For(origin, condition, pre, body, post);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_for (this);
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public Instruction get_pre ()
   {
      return pre;
   }

   public Instruction get_post ()
   {
      return post;
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

      sb.append("(For");
      sb.append(System.lineSeparator());
      sb.append(pre.toString());
      sb.append(System.lineSeparator());
      sb.append(condition.toString());
      sb.append(System.lineSeparator());
      sb.append(post.toString());

      for (final Instruction i: body)
      {
         sb.append(System.lineSeparator());
         sb.append(i.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
