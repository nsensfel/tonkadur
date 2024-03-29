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
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.ValueOf;
import tonkadur.wyrd.v1.lang.computation.Size;

import tonkadur.wyrd.v1.lang.instruction.Initialize;
import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.SetPC;
import tonkadur.wyrd.v1.lang.instruction.SetValue;

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
   protected final Register choice_number, rand_mode, rand_value;
   protected final List<Instruction> awaiting_inits;
   protected int created_contexts;

   public RegisterManager ()
   {
      awaiting_inits = new ArrayList<Instruction>();
      base_context = new RegisterContext("base context");
      parameter_context = new RegisterContext("parameter context", ".param.");

      next_pc = base_context.reserve(Type.INT, awaiting_inits);
      pc_stack = base_context.reserve(new MapType(Type.INT), awaiting_inits);

      choice_number = base_context.reserve(Type.INT, awaiting_inits);
      rand_mode = base_context.reserve(Type.INT, awaiting_inits);
      rand_value =
         base_context.reserve(new MapType(Type.INT), awaiting_inits);

      context_by_name = new HashMap<String, StackableRegisterContext>();
      context = new ArrayDeque<RegisterContext>();
      created_contexts = 0;

      context.push(base_context);
   }

   public Register reserve (final Type t, final List<Instruction> instr_holder)
   {
      return context.peekFirst().reserve(t, instr_holder);
   }

   public void release (final Register r, final List<Instruction> instr_holder)
   {
      context.peekFirst().release(r, instr_holder);
   }

   public String create_stackable_context_name ()
   {
      return context_name_prefix + (created_contexts++);
   }

   public Register get_choice_number_holder ()
   {
      return choice_number;
   }

   public Register get_rand_mode_holder ()
   {
      return rand_mode;
   }

   public Register get_rand_value_holder ()
   {
      return rand_value;
   }

   public void create_stackable_context
   (
      final String context_name,
      final List<Instruction> instr_holder
   )
   {
      final StackableRegisterContext result;

      result =
         new StackableRegisterContext
         (
            base_context,
            context_name,
            instr_holder
         );

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

   public Register register
   (
      final Type t,
      final String name,
      final List<Instruction> instr_holder
   )
   {
      return context.peekFirst().reserve(t, name, instr_holder);
   }

   public void bind (final String name, final Register register)
   {
      context.peekFirst().bind(name, register);
   }

   public void unbind (final String name, final List<Instruction> instr_holder)
   {
      context.peekFirst().unbind(name, instr_holder);
   }

   public void unbind_but_do_not_free (final String name)
   {
      context.peekFirst().unbind_but_do_not_free(name);
   }

   public Register get_context_register (final String name)
   {
      final Register result;

      result = context.peekFirst().get_register(name);

      if (result == null)
      {
         return base_context.get_non_local_register(name);
      }
      else if (!result.is_active())
      {
         System.err.println("[P] Inactive context register: " + name);
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
         new Initialize
         (
            new RelativeAddress
            (
               pc_stack.get_address(),
               new Cast(new Size(pc_stack.get_address()), Type.STRING),
               Type.INT
            ),
            Type.INT
         )
      );

      result.add
      (
         new SetValue
         (
            new RelativeAddress
            (
               pc_stack.get_address(),
               new Cast
               (
                  Operation.minus
                  (
                     new Size(pc_stack.get_address()),
                     Constant.ONE
                  ), Type.STRING
               ),
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
            new Cast
            (
               Operation.minus
               (
                  new Size(pc_stack.get_address()),
                  Constant.ONE
               ),
               Type.STRING
            ),
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

   public void push_hierarchical_instruction_level ()
   {
      context.peekFirst().push_hierarchical_instruction_level();
   }

   public void pop_hierarchical_instruction_level
   (
      final List<Instruction> instr_holder
   )
   {
      context.peekFirst().pop_hierarchical_instruction_level(instr_holder);
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

         r = parameter_context.reserve(p.get_type(), result);

         result.add(new SetValue(r.get_address(), p));

         used_registers.add(r);
      }

      for (final Register r: used_registers)
      {
         /* Side-channel attack to pass parameters, because it's convenient. */
         r.deactivate();
         // Do not use the context to deactivate here, otherwise the value will
         // be removed.
      }

      return result;
   }

   public List<Instruction> read_parameters (final List<Register> params)
   {
      final List<Register> used_registers;
      final List<Instruction> result;
      final List<Instruction> ignored_inits;

      used_registers = new ArrayList<Register>();
      result = new ArrayList<Instruction>();
      ignored_inits = new ArrayList<Instruction>();

      for (final Register p: params)
      {
         final Register r;

         r = parameter_context.reserve(p.get_type(), ignored_inits);

         result.add(new SetValue(p.get_address(), r.get_value()));

         used_registers.add(r);
      }

      for (final Register r: used_registers)
      {
         /* Side-channel attack to pass parameters, because it's convenient. */
         parameter_context.release(r, result);
      }

      return result;
   }

   public List<Instruction> get_initialization ()
   {
      return awaiting_inits;
   }
}
