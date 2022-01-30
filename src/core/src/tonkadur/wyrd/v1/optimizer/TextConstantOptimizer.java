package tonkadur.wyrd.v1.optimizer;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Text;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class TextConstantOptimizer
{
   private static String attempt_to_extract_string_constant
   (
      final Computation c
   )
   {
      if (c instanceof Constant)
      {
         final Constant v;

         v = (Constant) c;

         if (v.get_type().equals(Type.STRING))
         {
            return v.get_as_string();
         }
      }
      else if (c instanceof Text)
      {
         final Text t;

         t = (Text) c;

         if (t.get_content().size() == 1)
         {
            return attempt_to_extract_string_constant(t.get_content().get(0));
         }
      }

      return null;
   }

   public static void optimize (final List<Computation> content)
   {
      int i, content_size, acc_start_i;
      String accumulator;

      content_size = content.size();
      i = 0;
      acc_start_i = -1;

      accumulator = "";

      if (content.size() <= 1)
      {
         return;
      }

      while (i < content_size)
      {
         final String maybe_as_string;

         maybe_as_string = attempt_to_extract_string_constant(content.get(i));

         if (maybe_as_string == null)
         {
            if (acc_start_i == -1)
            {
               ++i;
            }
            else
            {
               content.set(acc_start_i, Constant.string_value(accumulator));

               i--;

               while (i > acc_start_i)
               {
                  content.remove(i);
                  i--;
                  content_size--;
               }

               i++;
               acc_start_i = -1;
            }
         }
         else
         {
            if (acc_start_i == -1)
            {
               acc_start_i = i;
               accumulator = "";
            }

            ++i;
            accumulator += maybe_as_string;
         }
      }

      if (acc_start_i == -1)
      {
         ++i;
      }
      else
      {
         content.set(acc_start_i, Constant.string_value(accumulator));

         i--;

         while (i > acc_start_i)
         {
            content.remove(i);
            i--;
         }

         i++;
         acc_start_i = -1;
      }
   }
}
