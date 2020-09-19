package tonkadur.fate.v1.lang.meta;

import java.util.Map;
import java.util.Deque;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.computation.VariableReference;
import tonkadur.fate.v1.lang.computation.FieldReference;

import tonkadur.fate.v1.lang.Variable;
import tonkadur.fate.v1.lang.World;

public class VariableFromWord
{
   /* Utility Class */
   private VariableFromWord () { }

   public static Reference generate
   (
      final World WORLD,
      final Deque<Map<String, Variable>> LOCAL_VARIABLES,
      final Origin origin,
      final String word
   )
   throws ParsingError
   {
      final String[] subrefs;
      Reference result;
      Variable target_var;

      subrefs = word.split("\\.");

      target_var = LOCAL_VARIABLES.peekFirst().get(subrefs[0]);

      if (target_var == null)
      {
         target_var = WORLD.variables().get(origin, subrefs[0]);
      }

      result = new VariableReference(origin, target_var);

      if (subrefs.length > 1)
      {
         final List<String> subrefs_list;

         subrefs_list = new ArrayList(Arrays.asList(subrefs));

         subrefs_list.remove(0);

         result =
            FieldReference.build
            (
               origin,
               result,
               subrefs_list
            );
      }

      return result;
   }
}
