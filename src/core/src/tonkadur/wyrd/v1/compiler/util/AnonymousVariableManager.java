package tonkadur.wyrd.v1.compiler.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.Variable;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.lang.type.Type;


public class AnonymousVariableManager
{
   protected static final String name_prefix = ".anon.";

   protected final Map<String, Cons<Boolean, Variable>> by_name;
   protected final Map<Type, List<Cons<Boolean, Variable>>> by_type;
   protected int generated_variables;

   public AnonymousVariableManager ()
   {
      by_name = new HashMap<String, Cons<Boolean, Variable>>();
      by_type = new HashMap<Type, List<Cons<Boolean, Variable>>>();
      generated_variables = 0;
   }

   public Ref reserve (final Type t)
   {
      final Ref result;
      final Variable new_variable;
      List<Cons<Boolean, Variable>> list;

      list = by_type.get(t);

      if (list == null)
      {
         list = new ArrayList<Cons<Boolean, Variable>>();

         by_type.put(t, list);
      }

      for (final Cons<Boolean, Variable> entry: list)
      {
         if (!entry.get_car())
         {
            result = entry.get_cdr().get_ref();

            entry.set_car(Boolean.TRUE);

            return result;
         }
      }

      new_variable =
         new Variable
         (
            (name_prefix + Integer.toString(generated_variables)),
            "local",
            t
         );

      list.add(new Cons(Boolean.TRUE, new_variable));

      return new_variable.get_ref();
   }

   public void release (final Ref r)
   {
      final Computation c;
      final String name;

      c = r.get_accesses().get(0);

      if (!(c instanceof Constant))
      {
         /* TODO: error */

         return;
      }

      name = ((Constant) c).get_as_string();

      by_name.get(name).set_car(Boolean.FALSE);
   }
}
