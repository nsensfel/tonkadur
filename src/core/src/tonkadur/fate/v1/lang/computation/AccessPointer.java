package tonkadur.fate.v1.lang.computation;

import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.Type;

public class AccessPointer extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference parent;
   protected final Computation index;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AccessPointer
   (
      final Origin origin,
      final Reference parent,
      final Type type,
      final Computation index
   )
   {
      super
      (
         origin,
         new PointerType(origin, type, "(ptr " + type.toString() + ") autogen")
      );

      this.parent = parent;
      this.index = index;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static AccessPointer build
   (
      final Origin origin,
      Reference parent,
      final Computation index
   )
   throws InvalidTypeException
   {
      Type current_type;

      current_type = parent.get_type();

      while (current_type.get_act_as_type().equals(Type.REF))
      {
         parent = AtReference.build(origin, parent);
         current_type = parent.get_type();
      }

      if (!index.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               index.get_origin(),
               current_type,
               Collections.singleton(Type.INT),
               index.toString()
            )
         );
      }

      if (!(current_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singleton(Type.LIST),
               parent.toString()
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((CollectionType) current_type).get_content_type();
      }

      return new AccessPointer(origin, parent, current_type, index);
   }


   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_access_pointer(this);
   }

   public Computation get_index ()
   {
      return index;
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

      sb.append("(AccessPointer (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent.get_name());
      sb.append(".");
      sb.append(index.toString());
      sb.append(")");

      return sb.toString();
   }
}
