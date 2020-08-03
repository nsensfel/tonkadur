package tonkadur.fate.v1.lang.computation;

import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownDictionaryFieldException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

import tonkadur.fate.v1.lang.type.DictType;
import tonkadur.fate.v1.lang.type.Type;

public class FieldReference extends Reference
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference parent;
   protected final String field_name;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected FieldReference
   (
      final Origin origin,
      final Reference parent,
      final Type type,
      final String field_name
   )
   {
      super(origin, type, (parent.get_name() + "." + field_name));

      this.parent = parent;
      this.field_name = field_name;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static FieldReference build
   (
      final Origin origin,
      Reference parent,
      final String field
   )
   throws
      InvalidTypeException,
      UnknownDictionaryFieldException
   {
      Type current_type;

      current_type = parent.get_type();

      if (current_type.get_act_as_type().equals(Type.REF))
      {
         parent = AtReference.build(origin, parent);
         current_type = parent.get_type();
      }

      if (!(current_type instanceof DictType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singleton(Type.DICT),
               parent.get_name()
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((DictType) current_type).get_field_type(origin, field);
      }

      return new FieldReference(origin, parent, current_type, field);
   }

   public static FieldReference build
   (
      final Origin origin,
      Reference parent,
      final List<String> field_sequence
   )
   throws
      InvalidTypeException,
      UnknownDictionaryFieldException
   {
      for (final String field: field_sequence)
      {
         parent = build(origin, parent, field);
      }

      if (parent instanceof FieldReference)
      {
         return (FieldReference) parent;
      }
      else
      {
         return null;
      }
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_field_reference(this);
   }

   public String get_field_name ()
   {
      return field_name;
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

      sb.append("(FieldReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }
}
