package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class Range extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:range");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation start;
      final Computation end;
      final Computation increment;

      if (call_parameters.size() != 3)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + " <start: INT> <end: INT> <increment: INT>)"
            )
         );

         return null;
      }

      start = call_parameters.get(0);
      end = call_parameters.get(1);
      increment = call_parameters.get(2);

      start.expect_non_string();
      end.expect_non_string();
      increment.expect_non_string();

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
   /**** Accessors ************************************************************/
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
