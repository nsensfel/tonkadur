package tonkadur.fate.v1.lang.instruction;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class SetValue extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final ValueNode element;
   protected final ValueNode value_reference;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SetValue
   (
      final Origin origin,
      final ValueNode element,
      final ValueNode value_reference
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
      final ValueNode element,
      final ValueNode value_reference
   )
   throws
      InvalidTypeException,
      ConflictingTypeException,
      IncomparableTypeException
   {
      final Type hint;
      final Type value_reference_type;

      value_reference_type = value_reference.get_type();

      if (element.get_type().can_be_used_as(value_reference_type))
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
