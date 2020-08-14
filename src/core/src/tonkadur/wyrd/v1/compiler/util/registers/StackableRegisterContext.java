package tonkadur.wyrd.v1.compiler.util.registers;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Cast;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.PointerType;
import tonkadur.wyrd.v1.lang.type.MapType;
import tonkadur.wyrd.v1.lang.type.DictType;


class StackableRegisterContext extends RegisterContext
{
   protected final Map<String, Type> context_structure_fields;
   protected final RegisterContext base_context;
   protected final DictType context_structure;
   protected final Register context_stack_level;
   protected final Register context_stacks;
   protected final Address current_context_address_holder;
   protected final Computation current_context_address;

   public StackableRegisterContext
   (
      final RegisterContext base_context,
      final String context_name
   )
   {
      super(context_name);

      this.base_context = base_context;

      context_structure_fields = new HashMap<String, Type>();

      context_structure = new DictType(context_name, context_structure_fields);

      context_stack_level = base_context.reserve(Type.INT);
      context_stacks =
         base_context.reserve
         (
            new MapType(new PointerType(context_structure))
         );

      current_context_address_holder =
         new RelativeAddress
         (
            context_stacks.get_address(),
            new Cast(context_stack_level.get_value(), Type.STRING),
            new PointerType(context_structure)
         );

      current_context_address = new ValueOf(current_context_address_holder);
   }

   @Override
   protected Register create_register (final Type t, final String name)
   {
      final Register result;

      if (context_structure.get_fields().get(name) != null)
      {
         System.err.println
         (
            "[P] Duplicate register '"
            + name
            + "' in stackable context "
            + this.name
            + "."
         );
      }

      context_structure.get_fields().put(name, t);

      return
         new Register
         (
            new RelativeAddress
            (
               current_context_address,
               new Constant(Type.STRING, name),
               t
            ),
            t,
            name
         );
   }

   public List<Instruction> get_initialize_instructions ()
   {
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      result.add
      (
         new SetValue
         (
            context_stack_level.get_address(),
            Operation.plus(context_stack_level.get_value, Constant.ONE)
         )
      );

      result.add
      (
         new SetValue
         (
            current_context_address_holder,
            new New(context_structure)
         )
      );

      return result;
   }

   public List<Instruction> get_finalize_instructions ()
   {
      final List<Instruction> result;

      result = new ArrayList<Instruction>();

      result.add(new Remove(current_context_address));
      result.add(new Remove(current_context_address_holder));

      result.add
      (
         new SetValue
         (
            context_stack_level.get_address(),
            Operation.minus(context_stack_level.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         new SetValue
         (
            context_stack_level.get_address(),
            Operation.minus(context_stack_level.get_value(), Constant.ONE)
         )
      );

      return result;
   }

   public DictType get_structure_type ()
   {
      return context_structure;
   }
}
