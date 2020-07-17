package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;
import tonkadur.fate.v1.lang.meta.InstructionNode;

public class Sequence extends DeclaredEntity
{
   @Override
   public /* static */ String get_type_name ()
   {
      return "Sequence";
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final InstructionNode root;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Sequence
   (
      final Origin origin,
      final InstructionNode root,
      final String name
   )
   {
      super(origin, name);

      this.root = root;
   }

   /**** Accessors ************************************************************/
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

      sb.append("(Sequence ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
}
