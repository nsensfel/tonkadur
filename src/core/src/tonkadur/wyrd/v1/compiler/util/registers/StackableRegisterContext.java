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
   protected final Map<String, RegisterContext> context_by_name;
   protected final Deque<RegisterContext> context;
   protected final RegisterContext base_context;

   public RegisterManager ()
   {
      base_context = new RegisterContext("base context", false);

      context_by_name = new HashMap<String, RegisterContext>();
      context = new ArrayDeque<RegisterContext>();

      context_by_name.put("base context", base_context);
      context.push(base_context);
   }

   public Ref reserve (final Type t)
   {

   }

   protected static class RegisterContext
   {
      protected final String name;
      protected final boolean is_stackable;
      protected final Map<String, Register> register_by_name;
      protected final Map<Type, Register> register_by_type;
      protected int generated_variables;

      public RegisterContext (final String name, final boolean is_stackable)
      {
         this.name = name;
         this.is_stackable = is_stackable;

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

      public Ref reserve (final Type t)
      {
         final String name;
         final Ref result;
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

      public void release (final Ref r)
      {
         final Computation c;
         final String name;

         c = r.get_address();

         if (!(c instanceof Constant))
         {
            System.err.println
            (
               "[P] Anonymous Variable '"
               + r.toString()
               + "' is not at constant address."
            );

            return;
         }

         name = ((Constant) c).get_as_string();

         by_name.get(name).set_car(Boolean.FALSE);
      }

      protected static class Register
      {
         protected boolean is_in_use;
         protected Variable variable;
      }
   }
}
