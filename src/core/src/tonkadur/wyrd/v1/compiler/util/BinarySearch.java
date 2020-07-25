package tonkadur.wyrd.v1.compiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.IfElseInstruction;
import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.While;

public class BinarySearch
{
   /*
      (set .found false)
      (declare_variable local int .top)
      (set .top (- (size collection) 1))
      (declare_variable local int .bot)
      (set .bot 0)
      (declare_variable local int .mid)
      (declare_variable local <element.type> .midval)
      (while (and (not (var .found)) (<= (variable .bot) (variable .top)))
         (set .mid
            (+
               (variable .bot)
               (cast float int
                  (/
                     (cast int float (- (variable .top) (variable .bot)))
                     2.0
                  )
               )
            )
         )
         (set .midval
            (value_of
               (relative_ref collection ( (cast int string .mid) ))
            )
         )
         (ifelse (< (variable .midval) (variable .anon1))
            (set .bot (+ (var .mid) 1))
            (ifelse (> (var .midval) (variable .anon1))
               (set .top (- (var .mid) 1))
               (set .found true)
            )
         )
      )
   */
   public static List<Instruction> generate
   (
      final AnonymousVariableManager anonymous_variables,
      final Ref target,
      final Ref collection,
      final Ref collection_size_or_null,
      final Ref result_was_found_holder,
      final Ref result_index_holder
   )
   {
      final List<Instruction> result, while_body;
      final Ref top, bot, midval;

      result = new ArrayList<Instruction>();
      while_body = new ArrayList<Instruction>();

      top = anonymous_variables.reserve(Type.INT);
      bot = anonymous_variables.reserve(Type.INT);
      midval = anonymous_variables.reserve(target.get_type());

      result.add(new SetValue(result_holder, Constant.FALSE));
      result.add
      (
         new SetValue
         (
            top,
            Operation.minus
            (
               (
                  (collection_size_or_null == null) ?
                  new Size(collection) : new ValueOf(collection_size_or_null)
               ),
               new Constant(Type.INT, "1")
            )
         )
      );
      result.add
      (
         new SetValue(bot, new Constant(Type.INT, "0"))
      );

      while_body.add
      (
         new SetValue
         (
            result_index_holder,
            Operation.plus
            (
               new ValueOf(bot)
               new Cast
               (
                  Operation.divide
                  (
                     new Cast
                     (
                        Operation.minus
                        (
                           new ValueOf(top)
                           new ValueOf(bot)
                        )
                        Type.FLOAT
                     ),
                     2
                  )
                  Type.INT
               )
            )
         )
      );
      while_body.add
      (
         new SetValue
         (
            midval,
            new ValueOf
            (
               new RelativeRef
               (
                  collection,
                  Collections.singletonList
                  (
                     new Cast(new ValueOf(result_index_holder), Type.STRING)
                  )
               )
            )
         )
      );
      while_body.add
      (
         new IfElseInstruction
         (
            Operation.less_than(new ValueOf(midval), new ValueOf(target)),
            Collections.singletonList
            (
               new SetValue
               (
                  bot,
                  Operation.plus
                  (
                     new ValueOf(result_index_holder),
                     new Constant(Type.INT, "1")
                  )
               )
            ),
            Collections.singletonList
            (
               new IfElseInstruction
               (
                  Operation.greater_than
                  (
                     new ValueOf(midval),
                     new ValueOf(target)
                  ),
                  Collections.singletonList
                  (
                     new SetValue
                     (
                        top,
                        Operation.minus
                        (
                           new ValueOf(result_index_holder),
                           new Constant(Type.INT, "1")
                        )
                     )
                  ),
                  Collections.singletonList
                  (
                     new SetValue(result_holder, Constant.TRUE)
                  )
               )
            )
         )
      );

      result.add
      (
         new While
         (
            Operation.and
            (
               Operation.not(new ValueOf(result_holder)),
               Operation.less_equal_than
               (
                  new ValueOf(bot),
                  new ValueOf(top)
               )
            ),
            while_body
         )
      );

      anonymous_variables.release(top);
      anonymous_variables.release(bot);
      anonymous_variables.release(midval);

      return result;
   }
}
