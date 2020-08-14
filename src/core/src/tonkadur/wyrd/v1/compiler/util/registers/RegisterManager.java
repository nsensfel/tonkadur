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
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.SetPC;
import tonkadur.wyrd.v1.lang.instruction.Remove;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.MapType;
import tonkadur.wyrd.v1.lang.type.DictType;

public class RegisterManager
{
   protected static final String context_name_prefix = ".context.";
   protected final Map<String, StackableRegisterContext> context_by_name;
   protected final Deque<RegisterContext> context;
   protected final RegisterContext base_context, parameter_context;
   protected final Register next_pc, pc_stack;
   protected int created_contexts;

   public RegisterManager ()
   {
      base_context = new RegisterContext("base context");
      parameter_context = new RegisterContext("parameter context", ".param.");
      next_pc = base_context.reserve(Type.INT);
      pc_stack = base_context.reserve(new MapType(Type.INT));

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

   public void push_context (final String context_name)
   {
      final StackableRegisterContext target;

      target = context_by_name.get(context_name);

      if (target == null)
      {
         System.err.println
         (
            "[P] Cannot push unknown context '"
            + context
            + "'."
         );
      }

      context.push(target);
   }

   public void pop_context ()
   {
      context.pop();
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

   public List<Instruction> get_visit_context_instructions
   (
      final Computation enter_at,
      final Computation leave_to
   )
   {
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      result.add
      (
         new SetValue
         (
            new RelativeAddress
            (
               pc_stack.get_address(),
               new Size(pc_stack.get_address()),
               Type.INT
            ),
            leave_to
         )
      );

      result.add(new SetPC(enter_at));

      return result;
   }

   public List<Instruction> get_jump_to_context_instructions
   (
      final Computation enter_at
   )
   {
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      result.add(new SetPC(enter_at));

      return result;
   }

   public List<Instruction> get_initialize_context_instructions
   (
      final String context_name
   )
   {
      return
         context_by_name.get(context_name).get_initialize_instructions();
   }

   public String get_current_context_name ()
   {
      return context.peekFirst().get_name();
   }

   public List<Instruction> get_leave_context_instructions ()
   {
      final Address return_to_address;
      final List<Instruction> result;

      return_to_address =
         new RelativeAddress
         (
            pc_stack.get_address(),
            new Size(pc_stack.get_address()),
            Type.INT
         );

      result = new ArrayList<Instruction>();

      result.add
      (
         new SetValue(next_pc.get_address(), new ValueOf(return_to_address))
      );

      result.add(new Remove(return_to_address));
      result.add(new SetPC(next_pc.get_value()));

      return result;
   }

   public List<Instruction> get_finalize_context_instructions ()
   {
      return context.peekFirst().get_finalize_instructions();
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

   public List<Instruction> store_parameters (final List<Computation> params)
   {
      final List<Register> used_registers;
      final List<Instruction> result;

      used_registers = new ArrayList<Register>();
      result = new ArrayList<Instruction>();

      for (final Computation p: params)
      {
         final Register r;

         r = parameter_context.reserve(p.get_type());

         result.add(new SetValue(r.get_address(), p));

         used_registers.add(r);
      }

      for (final Register r: used_registers)
      {
         /* Side-channel attack to pass parameters, because it's convenient. */
         r.set_is_in_use(false);
      }

      return result;
   }

   public List<Instruction> read_parameters (final List<Register> params)
   {
      final List<Register> used_registers;
      final List<Instruction> result;

      used_registers = new ArrayList<Register>();
      result = new ArrayList<Instruction>();

      for (final Register p: params)
      {
         final Register r;

         r = parameter_context.reserve(p.get_type());

         result.add(new SetValue(p.get_address(), r.get_value()));

         used_registers.add(r);
      }

      for (final Register r: used_registers)
      {
         /* Side-channel attack to pass parameters, because it's convenient. */
         r.set_is_in_use(false);
      }

      return result;
   }
}
