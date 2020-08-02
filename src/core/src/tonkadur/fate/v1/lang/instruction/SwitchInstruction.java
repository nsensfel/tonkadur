package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.functional.Cons;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class SwitchInstruction extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation target;
   protected final List<Cons<Computation, Instruction>> branches;
   protected final Instruction default_instruction;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SwitchInstruction
   (
      final Origin origin,
      final Computation target,
      final List<Cons<Computation, Instruction>> branches,
      final Instruction default_instruction
   )
   {
      super(origin);

      this.target = target;
      this.branches = branches;
      this.default_instruction = default_instruction;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static SwitchInstruction build
   (
      final Origin origin,
      final Computation target,
      final List<Cons<Computation, Instruction>> branches,
      final Instruction default_instruction
   )
   throws InvalidTypeException
   {
      final Type target_type;

      target_type = target.get_type();

      for (final Cons<Computation, Instruction> branch: branches)
      {
         if (!branch.get_car().get_type().get_base_type().equals(target_type))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  branch.get_car().get_origin(),
                  branch.get_car().get_type(),
                  Collections.singleton(target_type)
               )
            );
         }
      }

      return new SwitchInstruction(origin, target, branches, default_instruction);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_switch_instruction(this);
   }

   public Computation get_target ()
   {
      return target;
   }

   public List<Cons<Computation, Instruction>> get_branches ()
   {
      return branches;
   }

   public Instruction get_default_instruction ()
   {
      return default_instruction;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(SwitchInstruction");
      sb.append(System.lineSeparator());

      for (final Cons<Computation, Instruction> branch: branches)
      {
         sb.append(System.lineSeparator());
         sb.append("case:");
         sb.append(branch.get_car().toString());

         sb.append(System.lineSeparator());
         sb.append("then:");
         sb.append(branch.get_cdr().toString());
      }

      sb.append(System.lineSeparator());
      sb.append("default:");
      sb.append(default_instruction.toString());

      sb.append(")");

      return sb.toString();
   }
}
