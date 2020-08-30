package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class PromptInteger extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final Computation min;
   protected final Computation max;
   protected final Computation label;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PromptInteger
   (
      final Origin origin,
      final Computation target,
      final Computation min,
      final Computation max,
      final Computation label
   )
   {
      super(origin);

      this.target = target;
      this.min = min;
      this.max = max;
      this.label = label;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static PromptInteger build
   (
      final Origin origin,
      final Computation target,
      final Computation min,
      final Computation max,
      final Computation label
   )
   throws InvalidTypeException
   {
      if (!target.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               target.get_origin(),
               target.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      if (!min.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               min.get_origin(),
               min.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      if (!max.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               min.get_origin(),
               min.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      return new PromptInteger(origin, target, min, max, label);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_prompt_integer(this);
   }

   public Computation get_target ()
   {
      return target;
   }

   public Computation get_max ()
   {
      return max;
   }

   public Computation get_min ()
   {
      return min;
   }

   public Computation get_label ()
   {
      return label;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(PromptInteger ");
      sb.append(target.toString());
      sb.append(" ");
      sb.append(min.toString());
      sb.append(" ");
      sb.append(max.toString());
      sb.append(" ");
      sb.append(label.toString());
      sb.append(")");

      return sb.toString();
   }
}
