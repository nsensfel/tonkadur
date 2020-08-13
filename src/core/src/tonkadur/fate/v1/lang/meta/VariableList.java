package tonkadur.fate.v1.lang.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.DuplicateFieldException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.Variable;

public class VariableList
{
   protected final Map<String, Variable> as_map;
   protected final List<Variable> as_list;

   public VariableList ()
   {
      as_map = new HashMap<String, Variable>();
      as_list = new ArrayList<Variable>();
   }

   public void remove (final String name)
   {
      final Variable previous_entry;

      previous_entry = as_map.get(name);

      as_list.remove(previous_entry);
      as_map.remove(name);
   }

   public void add (final Variable entry)
   throws DuplicateFieldException
   {
      final Variable previous_entry;

      previous_entry = as_map.get(entry.get_name());

      if (previous_entry != null)
      {
         ErrorManager.handle
         (
            new DuplicateFieldException
            (
               entry.get_origin(),
               previous_entry.get_origin(),
               entry.get_name()
            )
         );
      }

      as_map.put(entry.get_name(), entry);
      as_list.add(entry);
   }

   public List<Variable> get_entries ()
   {
      return as_list;
   }

   public Map<String, Variable> as_map ()
   {
      return as_map;
   }
}
