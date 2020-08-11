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

   public Register reserve (final Type t)
   {
      context.peekFirst().reserve(t);
   }

   public void release (final Register r)
   {
      context.peekFirst().release(r);
   }

   public void create_stackable_context (final String context_name)
   {
      final StackableRegisterContext result;

      result = StackableRegisterContext.generate(base_context, context_name);

      context_by_name.put(result);
   }

   public void add_context_variable (final Type t, final String variable_name)
   {
      context.peekFirst().reserve(t, name);
   }

   public Register get_context_variable (final String variable_name)
   {
      context.peekFirst().get_variable(name);
   }

   public List<Instruction> get_enter_context_instructions
   (
      final String context_name
   )
   {
      return context_by_name.get(context_name).get_enter_instructions();
   }

   public List<Instruction> get_leave_context_instructions
   (
      final String context_name
   )
   {
      return context_by_name.get(context_name).get_leave_instructions();
   }

   public Collection<Type> get_generated_types ()
   {
      /* TODO */
      return null;
   }

   public Collection<Variable> get_generated_variables ()
   {
      /* TODO */
      return null;
   }
}
