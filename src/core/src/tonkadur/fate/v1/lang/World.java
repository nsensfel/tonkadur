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
   protected final Map<String, Event> events;
   protected final Map<String, Macro> macros;
   protected final Map<String, Sequence> sequences;
   protected final Map<String, TextEffect> text_effects;
   protected final Map<String, Type> types;
   protected final Map<String, Variable> variables;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public World ()
   {
      loaded_files = new HashSet<String>();

      events = new HashMap<String, Event>();
      macros = new HashMap<String, Macro>();
      sequences = new HashMap<String, Sequence>();
      text_effects = new HashMap<String, TextEffect>();
      types = new HashMap<String, Type>();
      variables = new HashMap<String, Variable>();

      add_base_types();
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

   /**** Events ****/
   public Collection<Event> get_events ()
   {
      return events.values();
   }

   public boolean has_event (final String name)
   {
      return events.containsKey(name);
   }

   public void add_event
   (
      final Origin origin,
      final String name,
      final List<Type> parameter_types
   )
   throws EventAlreadyDeclaredException, UnknownTypeException
   {
      if (has_event(name))
      {
         
      }
      for (final Type t: parameter_types)
      {
         if (!has_type(t))
         {
            throw new UnknownTypeException()
         }
      }
   }

   /**** Misc. ****************************************************************/

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected void add_base_types ()
   {
      final Origin base;

      base = new Origin(new Context(""), Location.BASE_LANGUAGE);

      add_type(base, null, "dict");
      add_type(base, null, "float");
      add_type(base, null, "int");
      add_type(base, null, "list");
      add_type(base, null, "set");
      add_type(base, null, "string");
   }
}
