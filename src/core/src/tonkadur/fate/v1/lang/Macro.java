package tonkadur.fate.v1.lang;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;
import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.TypedEntryList;

public class Macro extends DeclaredEntity
{
   @Override
   public /* static */ String get_type_name ()
   {
      return "Macro";
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final InstructionNode root;
   protected final TypedEntryList parameters;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Macro
   (
      final Origin origin,
      final InstructionNode root,
      final TypedEntryList parameters,
      final String name
   )
   {
      super(origin, name);

      this.root = root;
      this.parameters = parameters;
   }

   /**** Accessors ************************************************************/
   public TypedEntryList get_parameters ()
   {
      return parameters;
   }

   public InstructionNode get_root ()
   {
      return root;
   }

   /**** Compatibility ********************************************************/

   /*
    * This is for the very special case where a type is used despite not being
    * even a sub-type of the expected one. Using this rather expensive function,
    * the most restrictive shared type will be returned. If no such type exists,
    * the ANY time is returned.
    */
   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      return null;
   }


   /**** Misc. ****************************************************************/
   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      return true;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Macro ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
}
