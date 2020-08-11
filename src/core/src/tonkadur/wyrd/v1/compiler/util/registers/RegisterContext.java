package tonkadur.wyrd.v1.compiler.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.Variable;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Ref;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.lang.type.Type;


public class RegisterManager
{
   protected static final String name_prefix = ".anon.";
   protected final String name;
   protected final Map<String, Register> register_by_name;
   protected final Map<Type, Register> register_by_type;
   protected int generated_variables;

   public RegisterContext (final String name)
   {
      this.name = name;

      register_by_name = new HashMap<String, Register>();
      register_by_type = new HashMap<Type, Register>();

      generated_variables = 0;
   }

   public Collection<Variable> get_all_variables ()
   {
      final Collection<Variable> result;

      result = new ArrayList<Variable>();

      for (final Cons<Boolean, Variable> variable: by_name.values())
      {
         result.add(variable.get_cdr());
      }

      return result;
   }

   public Register reserve (final Type t)
   {
      final String name;
      final Register result;
      final Variable new_variable;
      final Cons<Boolean, Variable> status;
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

      name = (name_prefix + Integer.toString(generated_variables++));
      new_variable = new Variable(name, "local", t);
      status = new Cons(Boolean.TRUE, new_variable);

      list.add(status);

      by_name.put(name, status);

      return new_variable.get_ref();
   }

   public void release (final Register r)
   {
      r.set_is_in_use(false);
   }
}
