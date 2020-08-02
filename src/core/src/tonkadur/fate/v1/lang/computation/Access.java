package tonkadur.fate.v1.lang.computation;

import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownDictionaryFieldException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

public class Access extends Reference
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
   protected Access
   (
      final Origin origin,
      final Reference parent,
      final Type type,
      final Computation index
   )
   {
      super(origin, type, (parent.get_name() + "." + index.toString()));

      this.parent = parent;
      this.index = index;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Access build
   (
      final Origin origin,
      Reference parent,
      final Computation index
   )
   throws
      InvalidTypeException,
      UnknownDictionaryFieldException
   {
      Type current_type;

      current_type = parent.get_type();

      if (current_type.get_base_type().equals(Type.REF))
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
               parent.get_name()
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
               parent.get_name()
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((CollectionType) current_type).get_content_type();
      }

      return new Access(origin, parent, current_type, index);
   }


   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_access(this);
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

      sb.append("(Access (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(name);
      sb.append(".");
      sb.append(index.toString());
      sb.append(")");

      return sb.toString();
   }
}
