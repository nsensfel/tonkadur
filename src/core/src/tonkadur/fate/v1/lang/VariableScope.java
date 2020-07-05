package tonkadur.fate.v1.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/*
 * Yes, it *could* have been an enum. Except that you can't extend enums, making
 * them inadequate for anything that has even the slightest chance of needing to
 * be extended at some point in the future. In other words, they're inadequate
 * for anything but very rare corner cases (days in week, for example).
 * Tonkadur wants extension support, ergo, no enums in Tonkadur.
 *
 * The use of a collection to decode them stems from the same reason (can't
 * override static methods).
 */
public class VariableScope
{
   public static final VariableScope ANY;

   protected static final Map<String, VariableScope> from_name;
   public static final VariableScope LOCAL;
   public static final VariableScope GLOBAL;

   static
   {
      from_name = new HashMap<String, VariableScope>();

      ANY = new VariableScope("unknown scope", null);
      GLOBAL = new VariableScope("global", null);
      LOCAL = new VariableScope("local", GLOBAL);
   }

   public static VariableScope value_of (final String string)
   {
      return from_name.get(string);
   }

   public static Set<String> get_available_scopes ()
   {
      return from_name.keySet();
   }

   protected final String name;
   protected final Set<VariableScope> more_restrictive_than;

   protected VariableScope (final String name, final VariableScope parent)
   {
      this.name = name;

      more_restrictive_than = new HashSet<VariableScope>();

      if (parent != null)
      {
         more_restrictive_than.addAll(parent.more_restrictive_than);
         more_restrictive_than.add(parent);
      }

      from_name.put(name, this);
   }

   public VariableScope generate_compatible_with (final VariableScope other)
   {
      if (other.equals(this))
      {
         return this;
      }

      if (other.more_restrictive_than.contains(this))
      {
         if (this.more_restrictive_than.contains(other))
         {
            return ANY;
         }

         return other;
      }

      return this;
   }

   @Override
   public String toString ()
   {
      return name;
   }
}
