package tonkadur.wyrd.v1.compiler.util.registers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.instruction.Initialize;

class RegisterContext
{
   protected static final String default_name_prefix = ".anon.";
   protected final String name;
   protected final Map<String, Register> register_by_name;
   protected final Map<String, Register> aliased_registers;
   protected final Deque<Collection<String>> hierarchical_aliases;
   protected final List<Register> anonymous_registers;
   protected final String name_prefix;
   protected int generated_registers;

   public RegisterContext (final String name)
   {
      this.name = name;

      register_by_name = new HashMap<String, Register>();
      aliased_registers = new HashMap<String, Register>();
      hierarchical_aliases = new ArrayDeque<Collection<String>>();
      anonymous_registers = new ArrayList<Register>();
      name_prefix = default_name_prefix;

      generated_registers = 0;
   }

   public RegisterContext (final String name, final String name_prefix)
   {
      this.name = name;

      register_by_name = new HashMap<String, Register>();
      aliased_registers = new HashMap<String, Register>();
      anonymous_registers = new ArrayList<Register>();
      hierarchical_aliases = new ArrayDeque<Collection<String>>();
      this.name_prefix = name_prefix;

      generated_registers = 0;
   }

   public String get_name ()
   {
      return name;
   }

   public Collection<Register> get_all_registers ()
   {
      return register_by_name.values();
   }

   public Register reserve
   (
      final Type t,
      final List<Instruction> initialize_holder
   )
   {
      final String name;
      final Register result;

      for (final Register r: anonymous_registers)
      {
         if (!r.is_active())
         {
            r.activate(t);

            return r;
         }
      }

      name = (name_prefix + Integer.toString(generated_registers++));

      result = create_register(t, name, initialize_holder);

      anonymous_registers.add(result);

      register_by_name.put(name, result);

      return result;
   }

   public Register reserve
   (
      final Type t,
      final String name,
      final List<Instruction> initialize_holder
   )
   {
      final Register result;

      result = create_register(t, name, initialize_holder);

      if (register_by_name.get(name) != null)
      {
         System.err.println
         (
            "[P] Register '"
            + name
            + "' has multiple declarations within the same context."
         );
      }

      register_by_name.put(name, result);

      return result;
   }

   public void bind (final String name, final Register reg)
   {
      if (aliased_registers.containsKey(name))
      {
         System.err.println
         (
            "[P] Duplicate binding for register '"
            + name
            + "' in context '"
            + this.name
            + "'."
         );
      }

      aliased_registers.put(name, reg);

      if (!hierarchical_aliases.isEmpty())
      {
         hierarchical_aliases.peekFirst().add(name);
      }
   }

   public void unbind (final String name)
   {
      release(aliased_registers.get(name));

      aliased_registers.remove(name);

      if (!hierarchical_aliases.isEmpty())
      {
         hierarchical_aliases.peekFirst().remove(name);
      }
   }

   public void push_hierarchical_instruction_level ()
   {
      hierarchical_aliases.push(new ArrayList<String>());
   }

   public void pop_hierarchical_instruction_level ()
   {
      for (final String s: hierarchical_aliases.pop())
      {
         unbind(s);
      }
   }

   public Register get_register (final String name)
   {
      final Register result;

      result = aliased_registers.get(name);

      if (result != null)
      {
         return result;
      }

      return register_by_name.get(name);
   }

   public Register get_non_local_register (final String name)
   {
      return register_by_name.get(name);
   }

   public void release (final Register r)
   {
      r.deactivate();
   }

   protected Register create_register
   (
      final Type t,
      final String name,
      final List<Instruction> initialize_holder
   )
   {
      final Register result;

      result = new Register(name);
      result.activate(t);

      initialize_holder.add(new Initialize(result.get_address(), t));

      return result;
   }

   public List<Instruction> get_finalize_instructions ()
   {
      return new ArrayList();
   }
}
