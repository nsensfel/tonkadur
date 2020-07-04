package tonkadur.fate.v1.error;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class ConflictingDeclarationException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final DeclaredEntity new_declaration;
   protected final DeclaredEntity original_declaration;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public ConflictingDeclarationException
   (
      final DeclaredEntity new_declaration,
      final DeclaredEntity original_declaration
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.CONFLICTING_DECLARATION,
         new_declaration.get_origin()
      );

      this.new_declaration = new_declaration;
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
      sb.append(" conflicts with its declaration at ");
      sb.append(original_declaration.get_origin().get_location().toString());
      sb.append(".");
      sb.append(System.lineSeparator());
      sb.append("Previous declaration was ");
      sb.append(original_declaration.toString());
      sb.append(".");
      sb.append(System.lineSeparator());
      sb.append("New declaration is ");
      sb.append(new_declaration.toString());
      sb.append(".");

      return sb.toString();
   }
}
