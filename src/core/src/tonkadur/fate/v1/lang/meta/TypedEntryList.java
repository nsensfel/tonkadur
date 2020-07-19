package tonkadur.fate.v1.lang.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.DuplicateFieldException;

import tonkadur.fate.v1.lang.type.Type;

public class TypedEntryList
{
   protected final Map<String, TypedEntry> as_map;
   protected final List<TypedEntry> as_list;

   public TypedEntryList ()
   {
      as_map = new HashMap<String, TypedEntry>();
      as_list = new ArrayList<TypedEntry>();
   }

   public void add
   (
      final Origin origin,
      final Type type,
      final String name
   )
   throws DuplicateFieldException
   {
      TypedEntry previous_entry;

      previous_entry = as_map.get(name);

      if (previous_entry != null)
      {
         ErrorManager.handle
         (
            new DuplicateFieldException
            (
               origin,
               previous_entry.get_origin(),
               name
            )
         );
      }

      previous_entry = new TypedEntry(origin, type, name);

      as_map.put(name, previous_entry);
      as_list.add(previous_entry);
   }

   public List<TypedEntry> get_entries ()
   {
      return as_list;
   }

   public Map<String, TypedEntry> as_map ()
   {
      return as_map;
   }

   public static class TypedEntry
   {
      protected final Origin origin;
      protected final Type type;
      protected final String name;

      protected TypedEntry
      (
         final Origin origin,
         final Type type,
         final String name
      )
      {
         this.origin = origin;
         this.type = type;
         this.name = name;
      }

      public String get_name ()
      {
         return name;
      }

      public Type get_type ()
      {
         return type;
      }

      public Origin get_origin ()
      {
         return origin;
      }
   }
}
