package tonkadur.fate.v1.lang.valued_node;

import java.util.Arrays;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownDictionaryFieldException;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.ValueNode;

import tonkadur.fate.v1.lang.type.DictType;
import tonkadur.fate.v1.lang.type.Type;

public class VariableFieldReference extends VariableReference
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   final List<String> field_accesses;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected VariableFieldReference
   (
      final Origin origin,
      final Variable variable,
      final Type type,
      final List<String> field_accesses
   )
   {
      super(origin, type, variable);

      this.field_accesses = field_accesses;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static VariableFieldReference build
   (
      final Origin origin,
      final Variable variable,
      final List<String> field_accesses
   )
   throws
      InvalidTypeException,
      UnknownDictionaryFieldException
   {
      final StringBuilder sb;
      Type current_type;

      sb = new StringBuilder();

      current_type = variable.get_type();

      for (final String field_access: field_accesses)
      {
         sb.append(".");
         sb.append(field_access);

         if (!(current_type instanceof DictType))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  origin,
                  current_type,
                  Arrays.asList(new Type[]{Type.DICT}),
                  (variable.get_name() + "." + sb.toString())
               )
            );

            break;
         }

         current_type =
            ((DictType) current_type).get_field_type(origin, field_access);
      }

      return
         new VariableFieldReference
         (
            origin,
            variable,
            current_type,
            field_accesses
         );
   }

   /**** Accessors ************************************************************/
   public List<String> get_field_accesses ()
   {
      return field_accesses;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(VariableFieldReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(variable.get_name());

      for (final String field: field_accesses)
      {
         sb.append(".");
         sb.append(field);
      }

      sb.append(")");

      return sb.toString();
   }
}
