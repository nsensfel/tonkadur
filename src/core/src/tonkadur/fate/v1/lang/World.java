package tonkadur.fate.v1.lang;

import tonkadur.fate.v1.error.EventAlreadyDeclaredException;
import tonkadur.fate.v1.error.TypeAlreadyDeclaredException;
import tonkadur.fate.v1.error.TextEffectAlreadyDeclaredException;
import tonkadur.fate.v1.error.UnknownTypeException;

public class World
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Collection<String> loaded_files;
   protected final Collection<String> text_effects;
   protected final Map<String, Event> events;
   protected final Map<String, Macro> macros;
   protected final Map<String, Sequence> sequences;
   protected final Map<String, Type> types;
   protected final Map<String, Variable> variables;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public World ()
   {
      loaded_files = new HashSet<String>();
      text_effects = new HashSet<String>();

      events = new HashMap<String, Event>();
      macros = new HashMap<String, Macro>();
      sequences = new HashMap<String, Sequence>();
      types = new HashMap<String, Type>();
      variables = new HashMap<String, Variable>();

      for (final Type t: Type.BASE_TYPES)
      {
         types.add(t.get_name(), t);
      }
   }

   /**** Accessors ************************************************************/
   /**** Loaded Files ****/
   public Collection<String> get_loaded_files ()
   {
      return loaded_files.clone();
   }

   public boolean has_loaded_file (final String name)
   {
      return loaded_files.contains(name);
   }

   public void add_loaded_file (final String name)
   {
      loaded_files.add(name);
   }

   /**** Text Effects ****/
   public Collection<String> get_text_effects ()
   {
      return text_effects.clone();
   }

   public boolean has_text_effect (final String name)
   {
      return text_effects.contains(name);
   }

   public void add_text_effect (final String name)
   throws TextEffectAlreadyDeclaredException
   {
      text_effects.add(name);
   }

   /**** Events ****/
   public Collection<Event> get_events ()
   {
      return events.values();
   }

   public boolean has_event (final String name)
   {
      return events.containsKey(name);
   }

   public void add_event (final String name, final List<Type> parameter_types)
   throws EventAlreadyDeclaredException, UnknownTypeException
   {

   }

   /**** Misc. ****************************************************************/

   /***************************************************************************/
   /**** PRIVATE **************************************************************/
   /***************************************************************************/
}
