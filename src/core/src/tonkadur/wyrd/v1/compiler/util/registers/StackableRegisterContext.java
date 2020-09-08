package tonkadur.wyrd.v1.compiler.util.registers;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Operation;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Cast;
import tonkadur.wyrd.v1.lang.computation.New;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.instruction.SetValue;
import tonkadur.wyrd.v1.lang.instruction.Remove;
import tonkadur.wyrd.v1.lang.instruction.Initialize;

import tonkadur.wyrd.v1.lang.type.Type;
import tonkadur.wyrd.v1.lang.type.DictType;
import tonkadur.wyrd.v1.lang.type.PointerType;
import tonkadur.wyrd.v1.lang.type.MapType;


class StackableRegisterContext extends RegisterContext
{
   protected final RegisterContext base_context;
   protected final Register context_stack_level;
   protected final Register context_stacks;
   protected final Address current_context_address_holder;
   protected final Address current_context_address;

   public StackableRegisterContext
   (
      final RegisterContext base_context,
      final String context_name,
      final List<Instruction> initialize_holder
   )
   {
      super(context_name);

      this.base_context = base_context;

      context_stack_level = base_context.reserve(Type.INT, initialize_holder);
      context_stacks =
         base_context.reserve
         (
            new MapType(new PointerType(DictType.WILD)),
            initialize_holder
         );

      current_context_address_holder =
         new RelativeAddress
         (
            context_stacks.get_address(),
            new Cast(context_stack_level.get_value(), Type.STRING),
            new PointerType(DictType.WILD)
         );

      current_context_address =
            new Address
            (
               new ValueOf(current_context_address_holder),
               DictType.WILD
            );
   }

   @Override
   protected Register create_register (final Type t, final String name)
   {
      final Register result;

      result = new Register(current_context_address, name);

      result.activate(t);

      /* Handled elsewhere */
      //initialize_holder.add(new Initialize(result.get_address(), t));

      return result;
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
            Operation.plus(context_stack_level.get_value(), Constant.ONE)
         )
      );

      result.add
      (
         new Initialize
         (
            current_context_address_holder,
            new PointerType(DictType.WILD)
         )
      );

      result.add
      (
         new SetValue
         (
            current_context_address_holder,
            new New(DictType.WILD)
         )
      );

      return result;
   }

   @Override
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

      return result;
   }
}
