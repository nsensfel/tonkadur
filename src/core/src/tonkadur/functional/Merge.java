package tonkadur.functional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * I am not a fan of how Java implemented functional programming, so here is
 * some alternative.
 **/

public class Merge <Input0, Input1, Output>
{
   public List<Output> merge (final List<Input0> i0, final List<Input1> i1)
   {
      final int result_size;
      final List<Output> output;
      final Iterator<Input0> it0;
      final Iterator<Input1> it1;

      result_size = Math.max(i0.size(), i1.size());
      output = new ArrayList<Output>(result_size);

      it0 = i0.iterator();
      it1 = i1.iterator();

      for (int i = 0; i < result_size; ++i)
      {
         output.add
         (
            merge_fun
            (
               it0.hasNext() ? it0.next() : null,
               it1.hasNext() ? it1.next() : null
            )
         );
      }

      return output;
   }

   protected Output merge_fun (final Input0 i0, final Input1 i1)
   {
      return null;
   }
}
