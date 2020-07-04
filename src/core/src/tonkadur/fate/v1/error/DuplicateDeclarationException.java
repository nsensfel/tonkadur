package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class DuplicateDeclarationException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final DeclaredEntity original_declaration;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public DuplicateDeclarationException
   (
      final Origin origin,
      final DeclaredEntity original_declaration
   )
   {
      super(ErrorLevel.WARNING, ErrorCategory.DUPLICATE_DECLARATION, origin);
      this.original_declaration = original_declaration;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.get_context().toString());
      sb.append("Declaration for ");
      sb.append(original_declaration.get_type_name());
      sb.append(" '");
      sb.append(original_declaration.get_name());
      sb.append("' at ");
      sb.append(origin.get_location().toString());
      sb.append(" when it was already declared at ");
      sb.append(original_declaration.get_origin().get_location().toString());
      sb.append(".");

      return sb.toString();
   }
}
