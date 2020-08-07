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

public class ReverseList extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected ReverseList
   (
      final Origin origin,
      final Computation collection
   )
   {
      super(origin);

      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static ReverseList build
   (
      final Origin origin,
      final Computation collection
   )
   throws InvalidTypeException
   {
      final Type t;

      t = collection.get_type();

      if
      (
         !(t instanceof CollectionType)
         || ((CollectionType) t).is_set()
      )
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection.get_origin(),
               collection.get_type(),
               Collections.singleton(Type.LIST)
            )
         );
      }

      return new ReverseList(origin, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_reverse_list(this);
   }

   public Computation get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(ReverseList ");
      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
