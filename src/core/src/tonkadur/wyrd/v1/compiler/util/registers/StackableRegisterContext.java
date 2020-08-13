package tonkadur.wyrd.v1.compiler.util.registers;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.functional.Cons;

import tonkadur.wyrd.v1.lang.Variable;

import tonkadur.wyrd.v1.lang.meta.Computation;

import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.lang.type.Type;


class StackableRegisterContext extends RegisterContext
{
   protected final Map<String, Type> context_structure_fields;
   protected final DictType context_structure;
   protected final Register context_stack_level;
   protected final Register context_stacks;
   protected final Address current_context_address;

   public StackableRegisterContext
   (
      final RegisterContext base_context,
      final String context_name
   )
   {
      super(context_name);

      context_structure_fields = new HashMap<String, Type>();

      context_structure = new DictType(context_name, context_structure_fields);

      context_stack_level = base_context.reserve(Type.INT);
      context_stacks =
         base_context.reserve
         (
            new MapType(new PointerType(context_structure))
         );

      current_context_address =
         new RelativeAddress
         (
            context_stacks.get_address(),
            new Cast(context_stack_level.get_value(), Type.STRING),
            new PointerType(context_structure)
         );
   }

   @Override
   protected Register generate_register
   (
      final Type t,
      final String scope,
      final String name
   )
   {
      final Register result;

      if (context_structure.get(name) != null)
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

      context_structure.put(name, t);

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
            scope,
            name
         );
   }

   public DictType get_structure_type ()
   {
      return context_structure;
   }
}
