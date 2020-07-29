package tonkadur.wyrd.v1.compiler.fate.v1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tonkadur.wyrd.v1.lang.computation.Ref;

public class MacroManager
{
   protected final Stack<Map<String, Ref>> context_stack;

   public MacroManager ()
   {
      context_stack = new Stack<Map<String, Ref>>();
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
         parameters.put(pre.next().get_name(), pri.next());
      }
   }

   public Ref get_parameter_ref (final String parameter)
   {
      return context_stack.peek().get(parameter);
   }
}
