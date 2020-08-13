package tonkadur.wyrd.v1.compiler.util.registers;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.Type;

class RegisterContext
{
   protected static final String name_prefix = ".anon.";
   protected final String name;
   protected final Map<String, Register> register_by_name;
   protected final Map<String, Register> aliased_registers;
   protected final Map<Type, Register> anonymous_register_by_type;
   protected int generated_registers;

   public RegisterContext (final String name)
   {
      this.name = name;

      register_by_name = new HashMap<String, Register>();
      aliased_registers = new HashMap<String, Register>();
      anonymous_register_by_type = new HashMap<Type, Register>();

      generated_registers = 0;
   }

   public Collection<Register> get_all_registers ()
   {
      return register_by_name.values();
   }

   public Register reserve (final Type t)
   {
      final String name;
      final Register result;
      List<Register> list;

      list = anonymous_register_by_type.get(t);

      if (list == null)
      {
         list = new ArrayList<Register>();

         anonymous_register_by_type.put(t, list);
      }

      for (final register r: list)
      {
         if (!entry.get_is_in_use())
         {
            r.set_is_in_use(true);

            return r;
         }
      }

      name = (name_prefix + Integer.toString(generated_registers++));

      result = create_register(t, "local", name);

      result.set_is_in_use(true);

      list.add(result);

      register_by_name.put(name, result);

      return result;
   }

   public Register reserve (final Type t, final String scope, final String name)
   {
      final Register result;

      result = create_register(t, scope, name);

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

      result.set_is_in_use(true);

      return result;
   }

   public void bind (final Register reg, final String name)
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
   }

   public void unbind (final String name)
   {
      aliased_registers.remove(name);
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

   public void release (final Register r)
   {
      r.set_is_in_use(false);
   }

   protected Register create_register
   (
      final Type t,
      final String scope,
      final String name
   )
   {
      return new Register(t, scope, name);
   }
}
