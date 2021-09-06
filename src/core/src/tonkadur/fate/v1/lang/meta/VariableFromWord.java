package tonkadur.fate.v1.lang.meta;

import java.util.Map;
import java.util.Deque;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import tonkadur.error.ErrorLevel;
import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;
import tonkadur.parser.BasicParsingError;

import tonkadur.fate.v1.error.ErrorCategory;

import tonkadur.fate.v1.parser.ParserData;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.computation.Constant;
import tonkadur.fate.v1.lang.computation.FieldAccess;
import tonkadur.fate.v1.lang.computation.VariableReference;

import tonkadur.fate.v1.lang.computation.generic.AtReference;
import tonkadur.fate.v1.lang.computation.generic.Access;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.StructType;
import tonkadur.fate.v1.lang.type.Type;

public class VariableFromWord
{
   /* Utility Class */
   private VariableFromWord () { }

   public static Computation generate
   (
      final ParserData parser,
      final Origin origin,
      final String word
   )
   throws Throwable
   {
      final String[] subrefs;
      Computation result;
      Variable target_var;

      subrefs = word.split("\\.");

      target_var = parser.maybe_get_local_variable(subrefs[0]);

      if (target_var == null)
      {
         target_var = parser.get_world().variables().get(origin, subrefs[0]);
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

               result =
                  AtReference.build
                  (
                     origin.with_hint(subref),
                     "at",
                     Collections.singletonList(result)
                  );
            }

            if (t instanceof CollectionType)
            {
               result =
                  Access.build
                  (
                     origin.with_hint(subref),
                     "collection:access",
                     Arrays.asList
                     (
                        new Computation[]
                        {
                           Constant.build(origin.with_hint(subref), subref),
                           result
                        }
                     )
                  );
            }
            else if (t instanceof StructType)
            {
               result =
                  FieldAccess.build
                  (
                     origin.with_hint(subref),
                     result,
                     Collections.singletonList(subref)
                  );
            }
            else
            {
               ErrorManager.handle
               (
                  new BasicParsingError
                  (
                     ErrorLevel.ERROR,
                     ErrorCategory.INVALID_USE,
                     origin.with_hint(subref),
                     (
                        "Attempting to access a subreference from a value of"
                        + " type "
                        + t.toString()
                        + ", despite this type not being useable in this way."
                     )
                  )
               );
            }
         }
      }

      return result;
   }
}
