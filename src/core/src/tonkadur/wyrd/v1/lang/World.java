package tonkadur.wyrd.v1.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tonkadur.wyrd.v1.lang.Register;

import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.lang.meta.Instruction;

public class World
{
   protected final Set<String> required_extensions;

   protected final Collection<Register> registers;
   protected final Map<String, Integer> sequence_labels;
   protected final Map<String, DictType> dict_types;

   /* This solves the issue of using other yet undefined dict types. */
   protected final List<DictType> dict_types_in_order;

   protected final List<Instruction> code;

   public World ()
   {
      required_extensions = new HashSet<String>();

      registers = new ArrayList<Register>();
      sequence_labels = new HashMap<String, Integer>();
      dict_types = new HashMap<String, DictType>();
      dict_types_in_order = new ArrayList<DictType>();

      code = new ArrayList<Instruction>();
   }

   public void add_required_extension (final String name)
   {
      required_extensions.add(name);
   }

   public Set<String> get_required_extensions ()
   {
      return required_extensions;
   }

   public DictType get_dict_type (final String name)
   {
      return dict_types.get(name);
   }

   public List<DictType> get_ordered_dict_types ()
   {
      return dict_types_in_order;
   }

   public void add_dict_type (final DictType dict_type)
   {
      dict_types.put(dict_type.get_name(), dict_type);
      dict_types_in_order.add(dict_type);
   }

   public Collection<Register> get_registers ()
   {
      return registers;
   }

   public void add_register (final Register register)
   {
      registers.add(register);
   }

   public void add_sequence_label (final String name, final Integer line)
   {
      sequence_labels.put(name, line);
   }

   public Map<String, Integer> get_sequence_labels ()
   {
      return sequence_labels;
   }

   public void add_instruction (final Instruction i)
   {
      code.add(i);
   }

   public Integer get_current_line ()
   {
      return new Integer(code.size());
   }

   public List<Instruction> get_code ()
   {
      return code;
   }
}
