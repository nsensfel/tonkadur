package tonkadur.wyrd.v1.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tonkadur.wyrd.v1.lang.Variable;
import tonkadur.wyrd.v1.lang.Sequence;

import tonkadur.wyrd.v1.lang.type.DictType;

import tonkadur.wyrd.v1.lang.meta.Instruction;

public class World
{
   protected final Set<String> required_extensions;

   protected final Map<String, Variable> variables;
   protected final Map<String, Sequence> sequences;
   protected final Map<String, DictType> dict_types;

   /* This solves the issue of using other yet undefined dict types. */
   protected final List<DictType> dict_types_in_order;

   protected final List<Instruction> global_instructions;

   public World ()
   {
      required_extensions = new HashSet<String>();

      variables = new HashMap<String, Variable>();
      sequences = new HashMap<String, Sequence>();
      dict_types = new HashMap<String, DictType>();
      dict_types_in_order = new ArrayList<DictType>();

      global_instructions = new ArrayList<Instruction>();
   }

   public void add_required_extension (final String name)
   {
      required_extensions.add(name);
   }

   public DictType get_dict_type (final String name)
   {
      return dict_types.get(name);
   }

   public void add_dict_type (final DictType dict_type)
   {
      dict_types.put(dict_type.get_name(), dict_type);
      dict_types_in_order.add(dict_type);
   }

   public Variable get_variable (final String name)
   {
      return variables.get(name);
   }

   public void add_variable (final Variable variable)
   {
      variables.put(variable.get_name(), variable);
   }

   public Sequence get_sequence (final String name)
   {
      return sequences.get(name);
   }

   public void add_sequence (final Sequence sequence)
   {
      sequences.put(sequence.get_name(), sequence);
   }
}
