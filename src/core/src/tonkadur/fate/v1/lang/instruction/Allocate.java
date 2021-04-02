package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Reference;

public class Allocate extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference target;
   protected final Type allocated_type;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Allocate
   (
      final Origin origin,
      final Type allocated_type,
      final Reference target
   )
   {
      super(origin);

      this.allocated_type = allocated_type;
      this.target = target;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Allocate build (final Origin origin, final Reference target)
   throws ParsingError
   {
      final Type target_type;

      target_type = target.get_type();

      if (target_type instanceof PointerType)
      {
         return
            new Allocate
            (
               origin,
               ((PointerType) target_type).get_referenced_type(),
               target
            );
      }

      ErrorManager.handle
      (
         new InvalidTypeException
         (
            origin,
            target_type,
            Collections.singletonList
            (
               new PointerType(origin, Type.ANY, "Any Pointer")
            )
         )
      );

      return new Allocate(origin, Type.ANY, target);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_allocate(this);
   }

   public Reference get_target ()
   {
      return target;
   }

   public Type get_allocated_type ()
   {
      return allocated_type;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Allocate ");
      sb.append(target.toString());
      sb.append(")");

      return sb.toString();
   }
}
