package tonkadur.fate.v1.lang.computation;

import java.util.Collections;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownStructureFieldException;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Reference;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.Type;

public class AtReference extends Reference
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected AtReference
   (
      final Origin origin,
      final Type reported_type,
      final Computation parent
   )
   {
      super(origin, reported_type, ("(At " + parent.toString() + ")"));

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
      final Computation parent
   )
   throws
      InvalidTypeException
   {
      Type current_type;

      current_type = parent.get_type();

      if (!(current_type instanceof PointerType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singleton(Type.PTR)
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((PointerType) current_type).get_referenced_type();
      }

      return new AtReference(origin, current_type, parent);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_at_reference(this);
   }

   public Computation get_parent ()
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
      sb.append(parent.toString());
      sb.append(")");

      return sb.toString();
   }
}
