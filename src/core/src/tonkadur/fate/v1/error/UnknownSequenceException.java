package tonkadur.fate.v1.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tonkadur.functional.Cons;

import tonkadur.error.ErrorLevel;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.computation.SequenceReference;

public class UnknownSequenceException extends ParsingError
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Collection<Cons<Origin, List<Computation>>> all_occurrences;
   protected final String sequence_name;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public UnknownSequenceException
   (
      final Origin first_occurrence,
      final String sequence_name,
      final Collection<Cons<Origin, List<Computation>>> all_occurrences
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.UNKNOWN,
         first_occurrence
      );

      this.sequence_name = sequence_name;
      this.all_occurrences = all_occurrences;
   }

   public UnknownSequenceException
   (
      final String sequence_name,
      final Collection<SequenceReference> all_occurrences
   )
   {
      super
      (
         ErrorLevel.ERROR,
         ErrorCategory.UNKNOWN,
         all_occurrences.iterator().next().get_origin()
      );

      this.sequence_name = sequence_name;

      this.all_occurrences = new ArrayList<Cons<Origin, List<Computation>>>();

      for (final SequenceReference occ: all_occurrences)
      {
         this.all_occurrences.add
         (
            new Cons(occ.get_origin(), Collections.singleton(occ))
         );
      }
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append(" ");
      sb.append(error_category.toString());
      sb.append(System.lineSeparator());

      sb.append("Unknown sequence '");
      sb.append(sequence_name);
      sb.append("'. ");
      sb.append(System.lineSeparator());

      if (all_occurrences != null)
      {
         sb.append("Here is a complete list of all calls to this sequence:");
         sb.append(System.lineSeparator());

         for (final Cons<Origin, List<Computation>> occurrence: all_occurrences)
         {
            /*
             * Because sequences can be defined at any point, the context does
             * not matter much, so let's just point out locations instead.
             */
            sb.append("- ");
            sb.append(occurrence.get_car().get_location().toString());
            sb.append(System.lineSeparator());
         }
      }

      return sb.toString();
   }
}
