package tonkadur.fate.v1.lang.instruction;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;

public class Range extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;
   protected final Computation start;
   protected final Computation end;
   protected final Computation increment;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Range
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Computation increment,
      final Computation collection
   )
   {
      super(origin);

      this.collection = collection;
      this.start = start;
      this.end = end;
      this.increment = increment;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Range build
   (
      final Origin origin,
      final Computation start,
      final Computation end,
      final Computation increment,
      final Computation collection
   )
   throws InvalidTypeException
   {
      final Type t;

      t = collection.get_type();

      if (!(t instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection.get_origin(),
               collection.get_type(),
               Type.COLLECTION_TYPES
            )
         );
      }

      if (!start.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               start.get_origin(),
               start.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      if (!end.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               end.get_origin(),
               end.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      if (!increment.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               increment.get_origin(),
               increment.get_type(),
               Collections.singletonList(Type.INT)
            )
         );
      }

      return new Range(origin, start, end, increment, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_range(this);
   }

   public Computation get_collection ()
   {
      return collection;
   }

   public Computation get_start ()
   {
      return start;
   }

   public Computation get_end ()
   {
      return end;
   }

   public Computation get_increment ()
   {
      return increment;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Range ");
      sb.append(start.toString());
      sb.append(" ");
      sb.append(end.toString());
      sb.append(" ");
      sb.append(increment.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
