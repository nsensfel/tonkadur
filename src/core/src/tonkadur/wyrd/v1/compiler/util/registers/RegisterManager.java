package tonkadur.wyrd.v1.compiler.util.registers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.DictType;


public class RegisterManager
{
   protected static final String context_name_prefix = ".context.";
   protected final Map<String, StackableRegisterContext> context_by_name;
   protected final Deque<RegisterContext> context;
   protected final RegisterContext base_context;
   protected int created_contexts;

   public RegisterManager ()
   {
      base_context = new RegisterContext("base context");

      context_by_name = new HashMap<String, StackableRegisterContext>();
      context = new ArrayDeque<RegisterContext>();
      created_contexts = 0;

      context.push(base_context);
   }

   public Register reserve (final Type t)
   {
      return context.peekFirst().reserve(t);
   }

   public void release (final Register r)
   {
      context.peekFirst().release(r);
   }

   public String create_stackable_context_name ()
   {
      return context_name_prefix + (created_contexts++);
   }

   public void create_stackable_context (final String context_name)
   {
      final StackableRegisterContext result;

      result = new StackableRegisterContext(base_context, context_name);

      if (context_by_name.containsKey(context_name))
      {
         System.err.println("[P] Duplicate context '" + context_name +"'.");
      }

      context_by_name.put(context_name, result);
   }

   public Register register (final Type t, final String name)
   {
      return context.peekFirst().reserve(t, name);
   }

   public void bind (final String name, final Register register)
   {
      context.peekFirst().bind(name, register);
   }

   public void unbind (final String name)
   {
      context.peekFirst().unbind(name);
   }

   public Register get_context_register (final String name)
   {
      final Register result;

      result = context.peekFirst().get_register(name);

      if (result == null)
      {
         return base_context.get_register(name);
      }

      return result;
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

   public Collection<DictType> get_context_structure_types ()
   {
      final Collection<DictType> result;

      result = new ArrayList<DictType>();

      for (final StackableRegisterContext src: context_by_name.values())
      {
         result.add(src.get_structure_type());
      }

      return result;
   }

   public Collection<Register> get_base_registers ()
   {
      return base_context.get_all_registers();
   }
}
