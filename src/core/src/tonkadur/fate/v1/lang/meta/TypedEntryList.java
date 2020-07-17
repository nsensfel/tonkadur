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
   protected final Map<String, Origin> entry_origins;
   protected final List<TypedEntry> entries;

   public TypedEntryList ()
   {
      entry_origins = new HashMap<String, Origin>();
      entries = new ArrayList<TypedEntry>();
   }

   public void add
   (
      final Origin origin,
      final Type type,
      final String name
   )
   throws DuplicateFieldException
   {
      final Origin previous_origin;

      previous_origin = entry_origins.get(name);

      if (previous_origin != null)
      {
         ErrorManager.handle
         (
            new DuplicateFieldException(origin, previous_origin, name)
         );
      }

      entry_origins.put(name, origin);
      entries.add(new TypedEntry(origin, type, name));
   }

   public List<TypedEntry> get_entries ()
   {
      return entries;
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
