package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class Range extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
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
      final Type target_type
   )
   {
      super(origin, target_type);

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
      final Computation increment
   )
   throws ParsingError
   {
      RecurrentChecks.assert_can_be_used_as(start, Type.INT);
      RecurrentChecks.assert_can_be_used_as(end, Type.INT);
      RecurrentChecks.assert_can_be_used_as(increment, Type.INT);

      return
         new Range
         (
            origin,
            start,
            end,
            increment,
            CollectionType.build(origin, Type.INT, false, "auto generated")
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_range(this);
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
      sb.append(")");

      return sb.toString();
   }
}
