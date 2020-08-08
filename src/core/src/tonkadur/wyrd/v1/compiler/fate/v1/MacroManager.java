package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tonkadur.wyrd.v1.lang.computation.Ref;

public class MacroManager
{
   protected final Map<String, Ref> wild_parameters;
   protected final Stack<Map<String, Ref>> context_stack;

   public MacroManager ()
   {
      wild_parameters = new HashMap<String, Ref>();
      context_stack = new Stack<Map<String, Ref>>();
   }

   public void add_wild_parameter (final String name, final Ref ref)
   {
      if (wild_parameters.containsKey(name))
      {
         System.err.println("[P] duplicate wild parameter '" + name + "'.");

         return;
      }

      wild_parameters.put(name, ref);
   }

   public void remove_wild_parameter (final String name)
   {
      wild_parameters.remove(name);
   }

   public void pop ()
   {
      context_stack.pop();
   }

   public void push
   (
      final tonkadur.fate.v1.lang.Macro macro,
      final List<Ref> parameter_refs
   )
   {
      final Iterator<Ref> pri;
      final Iterator<tonkadur.fate.v1.lang.meta.TypedEntryList.TypedEntry> pre;
      final Map<String, Ref> parameters;

      parameters = new HashMap<String, Ref>();

      pri = parameter_refs.iterator();
      pre = macro.get_parameters().get_entries().iterator();

      while (pri.hasNext())
      {
         final String name;

         name = pre.next().get_name();

         parameters.put(name, pri.next());
      }

      context_stack.push(parameters);
   }

   public Ref get_parameter_ref (final String parameter)
   {
      Ref result;

      result = wild_parameters.get(parameter);

      if (result == null)
      {
         if (!context_stack.isEmpty())
         {
            result = context_stack.peek().get(parameter);
         }

         if (result == null)
         {
            System.err.println("[P] No such parameter '" + parameter + "'.");
         }
      }

      return result;
   }
}
