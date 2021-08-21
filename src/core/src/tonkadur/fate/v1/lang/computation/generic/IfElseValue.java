package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.FutureType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class IfElseValue extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("ifelse");
      aliases.add("if_else");
      aliases.add("ifElse");

      return aliases;
   }

   public static GenericComputation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      if (call_parameters.size() != 3)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      return
         build
         (
            origin,
            call_parameters.get(0),
            call_parameters.get(1),
            call_parameters.get(2)
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation condition;
   protected final Computation if_true;
   protected final Computation if_false;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IfElseValue
   (
      final Origin origin,
      final Type return_type,
      final Computation condition,
      final Computation if_true,
      final Computation if_false
   )
   {
      super(origin, return_type);

      this.condition = condition;
      this.if_true = if_true;
      this.if_false = if_false;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static IfElseValue build
   (
      final Origin origin,
      final Computation condition,
      final Computation if_true,
      final Computation if_false
   )
   throws ParsingError
   {
      condition.expect_non_string();

      RecurrentChecks.assert_can_be_used_as(condition, Type.BOOL);

      return
         new IfElseValue
         (
            origin,
            new FutureType(origin),
            condition,
            if_true,
            if_false
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void expect_non_string ()
   throws ParsingError
   {
      if_true.expect_non_string();
      if_false.expect_non_string();

      ((FutureType) get_type()).resolve_to
      (
         RecurrentChecks.assert_can_be_used_as(if_false, if_true.get_type())
      );
   }

   @Override
   public void expect_string ()
   throws ParsingError
   {
      if_true.expect_string();
      if_false.expect_string();

      ((FutureType) get_type()).resolve_to
      (
         RecurrentChecks.assert_can_be_used_as(if_false, if_true.get_type())
      );
   }

   public Computation get_condition ()
   {
      return condition;
   }

   public Computation get_if_true ()
   {
      return if_true;
   }

   public Computation get_if_false ()
   {
      return if_false;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IfElseValue");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Condition:");
      sb.append(System.lineSeparator());
      sb.append(condition.toString());

      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("If true:");
      sb.append(System.lineSeparator());
      sb.append(if_true.toString());

      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("If false:");
      sb.append(System.lineSeparator());
      sb.append(if_false.toString());

      sb.append(")");

      return sb.toString();
   }
}
