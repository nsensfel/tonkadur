package tonkadur.fate.v1.lang.meta;

import java.util.Map;
import java.util.Deque;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.Variable;
import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.computation.AccessAsReference;
import tonkadur.fate.v1.lang.computation.Constant;
import tonkadur.fate.v1.lang.computation.FieldReference;
import tonkadur.fate.v1.lang.computation.VariableReference;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.StructType;
import tonkadur.fate.v1.lang.type.Type;

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

      /* TODO don't rely on the other classes to generate the subrefs. */
      if (subrefs.length > 1)
      {
         Type t;
         final List<String> subrefs_list;

         subrefs_list = new ArrayList(Arrays.asList(subrefs));

         subrefs_list.remove(0);

         for (final String subref: subrefs_list)
         {
            t = result.get_type();

            while (t instanceof PointerType)
            {
               t = ((PointerType) t).get_referenced_type();
            }

            if (t instanceof CollectionType)
            {
               result =
                  AccessAsReference.build
                  (
                     origin,
                     result,
                     Constant.build(origin, subref)
                  );
            }
            else if (t instanceof StructType)
            {
               result =
                  FieldReference.build
                  (
                     origin,
                     result,
                     Collections.singletonList(subref)
                  );
            }
            else
            {
               /* TODO: error */
               System.err.println("Unimplemented error in VariableFromWord.");

               System.exit(-1);
            }
         }
      }

      return result;
   }
}
