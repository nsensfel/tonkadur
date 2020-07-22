package tonkadur.fate.v1.lang.valued_node;

import java.util.Collections;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownDictionaryFieldException;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

import tonkadur.fate.v1.lang.type.RefType;
import tonkadur.fate.v1.lang.type.Type;

public class AtReference extends Reference
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference parent;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected AtReference
   (
      final Origin origin,
      final Type reported_type,
      final Reference parent
   )
   {
      super(origin, reported_type, ("(At " + parent.get_name() + ")"));

      this.parent = parent;
   }
   /**** Constructors *********************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AtReference build
   (
      final Origin origin,
      final Reference parent
   )
   throws
      InvalidTypeException
   {
      Type current_type;

      current_type = parent.get_type();

      if (!(current_type instanceof RefType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singleton(Type.REF),
               parent.get_name()
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((RefType) current_type).get_referenced_type();
      }

      return new AtReference(origin, current_type, parent);
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_at_reference(this);
   }

   public Reference get_parent ()
   {
      return parent;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AtReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent.get_name());
      sb.append(")");

      return sb.toString();
   }
}
