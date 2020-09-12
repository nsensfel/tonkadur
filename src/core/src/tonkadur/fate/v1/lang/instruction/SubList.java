package tonkadur.fate.v1.lang.instruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

public class SubList extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation start;
   protected final Computation end;
   protected final Reference collection_in;
   protected final Reference collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected SubList
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Reference collection_in,
      final Reference collection_out
   )
   {
      super(origin);

      this.start = start;
      this.end = end;
      this.collection_in = collection_in;
      this.collection_out = collection_out;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static SubList build
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Reference collection_in,
      final Reference collection_out
   )
   throws Throwable
   {
      final Type collection_generic_type;

      collection_generic_type = collection_in.get_type();

      if (!(collection_generic_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection_in.get_origin(),
               collection_generic_type,
               Type.COLLECTION_TYPES
            )
         );

         return null;
      }

      if (!collection_generic_type.can_be_used_as(collection_out.get_type()))
      {
         /* TODO */
      }

      return new SubList(origin, start, end, collection_in, collection_out);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_sublist(this);
   }

   public Computation get_start_index ()
   {
      return start;
   }

   public Computation get_end_index ()
   {
      return end;
   }

   public Reference get_collection_in ()
   {
      return collection_in;
   }

   public Reference get_collection_out ()
   {
      return collection_out;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(SubList ");
      sb.append(start.toString());
      sb.append(" ");
      sb.append(end.toString());
      sb.append(" ");
      sb.append(collection_in.toString());
      sb.append(" ");
      sb.append(collection_out.toString());
      sb.append(")");

      return sb.toString();
   }
}
