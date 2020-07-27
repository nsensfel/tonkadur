package tonkadur.fate.v1.lang.instruction;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class SetValue extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation element;
   protected final Computation value_reference;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SetValue
   (
      final Origin origin,
      final Computation element,
      final Computation value_reference
   )
   {
      super(origin);

      this.value_reference = value_reference;
      this.element = element;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static SetValue build
   (
      final Origin origin,
      final Computation element,
      final Computation value_reference
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type hint;
      final Type value_reference_type;

      value_reference_type = value_reference.get_type();

      if
      (
         element.get_type().can_be_used_as(value_reference_type)
         ||
         (element.get_type().try_merging_with(value_reference_type) != null)
      )
      {
         return new SetValue(origin, element, value_reference);
      }


      ErrorManager.handle
      (
         new ConflictingTypeException
         (
            element.get_origin(),
            element.get_type(),
            value_reference_type
         )
      );

      hint =
         (Type) element.get_type().generate_comparable_to
         (
            value_reference_type
         );

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncomparableTypeException
            (
               element.get_origin(),
               element.get_type(),
               value_reference_type
            )
         );
      }

      return new SetValue(origin, element, value_reference);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_set_value(this);
   }

   public Computation get_value ()
   {
      return element;
   }

   public Computation get_reference ()
   {
      return value_reference;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(SetValue");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("element:");
      sb.append(System.lineSeparator());
      sb.append(element.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("value_reference:");
      sb.append(System.lineSeparator());
      sb.append(value_reference.toString());

      sb.append(")");

      return sb.toString();
   }
}
