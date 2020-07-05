package tonkadur.fate.v1.lang;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclarationCollection;

public class World
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Collection<String> loaded_files;
   protected final DeclarationCollection<Event> event_collection;
//   protected final DeclarationCollection<Macro> macros;
//   protected final DeclarationCollection<Sequence> sequences;
//   protected final DeclarationCollection<TextEffect> text_effects;
   protected final DeclarationCollection<Type> type_collection;
//   protected final DeclarationCollection<Variable> variables;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public World ()
   {
      loaded_files = new HashSet<String>();

      event_collection =
         new DeclarationCollection<Event>(Event.value_on_missing());
      //macros = new DeclarationCollection<Macro>();
      //sequences = new DeclarationCollection<Sequence>();
      //text_effects = new DeclarationCollection<TextEffect>();
      type_collection =
         new DeclarationCollection<Type>(Type.value_on_missing());
      //variables = new DeclarationCollection<Variable>();

      add_base_types();
   }

   /**** Accessors ************************************************************/
   /**** Loaded Files ****/
   public Collection<String> get_loaded_files ()
   {
      return loaded_files;
   }

   public boolean has_loaded_file (final String name)
   {
      return loaded_files.contains(name);
   }

   public void add_loaded_file (final String name)
   {
      loaded_files.add(name);
   }

   public DeclarationCollection<Type> types()
   {
      return type_collection;
   }

   public DeclarationCollection<Event> events()
   {
      return event_collection;
   }

   /**** Misc. ****************************************************************/

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected void add_base_types ()
   {

      try
      {
         type_collection.add(Type.BOOLEAN);
         type_collection.add(Type.DICT);
         type_collection.add(Type.FLOAT);
         type_collection.add(Type.INT);
         type_collection.add(Type.LIST);
         type_collection.add(Type.SET);
         type_collection.add(Type.STRING);
      }
      catch (final Throwable t)
      {
         System.err.println("Unable to add base types:" + t.toString());
         System.exit(-1);
      }

   }
}
