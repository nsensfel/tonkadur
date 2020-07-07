package tonkadur.fate.v1.lang;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.DeclarationCollection;

public class World
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Set<String> loaded_files;
   protected final Set<String> required_extensions;
   protected final DeclarationCollection<Event> event_collection;
//   protected final DeclarationCollection<Macro> macros;
//   protected final DeclarationCollection<Sequence> sequences;
   protected final DeclarationCollection<TextEffect> text_effect_collection;
   protected final DeclarationCollection<Type> type_collection;
   protected final DeclarationCollection<Variable> variable_collection;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public World ()
   {
      loaded_files = new HashSet<String>();
      required_extensions = new HashSet<String>();

      event_collection =
         new DeclarationCollection<Event>(Event.value_on_missing());
      //macros = new DeclarationCollection<Macro>();
      //sequences = new DeclarationCollection<Sequence>();
      text_effect_collection =
         new DeclarationCollection<TextEffect>(TextEffect.value_on_missing());
      type_collection =
         new DeclarationCollection<Type>(Type.value_on_missing());
      variable_collection =
         new DeclarationCollection<Variable>(Variable.value_on_missing());

      add_base_types();
   }

   /**** Accessors ************************************************************/
   /**** Loaded Files ****/
   public Set<String> get_loaded_files ()
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

   /**** Required Extensions ****/
   public Set<String> get_required_extensions ()
   {
      return required_extensions;
   }

   public boolean requires_extension (final String name)
   {
      return required_extensions.contains(name);
   }

   public void add_required_extension (final String name)
   {
      required_extensions.add(name);
   }

   /**** Collections ****/
   public DeclarationCollection<Event> events ()
   {
      return event_collection;
   }

   public DeclarationCollection<TextEffect> text_effects ()
   {
      return text_effect_collection;
   }

   public DeclarationCollection<Type> types ()
   {
      return type_collection;
   }

   public DeclarationCollection<Variable> variables ()
   {
      return variable_collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(World");
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());
      sb.append("Loaded files: ");
      sb.append(System.lineSeparator());

      for (final String filename: loaded_files)
      {
         sb.append("- ");
         sb.append(filename);
         sb.append(System.lineSeparator());
      }

      sb.append(System.lineSeparator());
      sb.append("Required Extensions: ");
      sb.append(System.lineSeparator());

      for (final String filename: required_extensions)
      {
         sb.append("- ");
         sb.append(filename);
         sb.append(System.lineSeparator());
      }

      sb.append(System.lineSeparator());
      sb.append("Events: ");
      sb.append(System.lineSeparator());
      sb.append(event_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Text Effects: ");
      sb.append(System.lineSeparator());
      sb.append(text_effect_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Types: ");
      sb.append(System.lineSeparator());
      sb.append(type_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Variables: ");
      sb.append(System.lineSeparator());
      sb.append(variable_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append(")");

      return sb.toString();
   }

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
