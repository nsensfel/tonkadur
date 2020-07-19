package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.UnknownSequenceException;

import tonkadur.fate.v1.lang.meta.DeclarationCollection;
import tonkadur.fate.v1.lang.meta.ExtensionInstruction;
import tonkadur.fate.v1.lang.meta.ExtensionValueNode;
import tonkadur.fate.v1.lang.meta.InstructionNode;

import tonkadur.fate.v1.lang.type.Type;

public class World
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Set<String> loaded_files;
   protected final Set<String> required_extensions;

   protected final Map<String, List<Origin>> sequence_calls;
   protected final Map<String, ExtensionValueNode> extension_value_nodes;
   protected final Map<String, ExtensionInstruction> extension_instructions;
   protected final Map<String, ExtensionInstruction>
      extension_first_level_instructions;

   protected final DeclarationCollection<Event> event_collection;
   protected final DeclarationCollection<Macro> macro_collection;
   protected final DeclarationCollection<Sequence> sequence_collection;
   protected final DeclarationCollection<TextEffect> text_effect_collection;
   protected final DeclarationCollection<Type> type_collection;
   protected final DeclarationCollection<Variable> variable_collection;

   protected final List<InstructionNode> global_instructions;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public World ()
   {
      loaded_files = new HashSet<String>();
      required_extensions = new HashSet<String>();

      sequence_calls = new HashMap<String, List<Origin>>();
      extension_value_nodes = new HashMap<String, ExtensionValueNode>();
      extension_instructions = new HashMap<String, ExtensionInstruction>();
      extension_first_level_instructions =
         new HashMap<String, ExtensionInstruction>();

      event_collection =
         new DeclarationCollection<Event>(Event.value_on_missing());
      macro_collection = new DeclarationCollection<Macro>(null);
      sequence_collection = new DeclarationCollection<Sequence>(null);

      text_effect_collection =
         new DeclarationCollection<TextEffect>(TextEffect.value_on_missing());
      type_collection =
         new DeclarationCollection<Type>(Type.value_on_missing());
      variable_collection =
         new DeclarationCollection<Variable>(Variable.value_on_missing());

      add_base_types();

      global_instructions = new ArrayList<InstructionNode>();
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

   /**** Sequence Calls ****/
   public void add_sequence_call (final Origin origin, final String sequence)
   {
      List<Origin> list_of_calls;

      list_of_calls = sequence_calls.get(sequence);

      if (list_of_calls == null)
      {
         list_of_calls = new ArrayList<Origin>();

         sequence_calls.put(sequence, list_of_calls);
      }

      list_of_calls.add(origin);
   }

   /**** Extension Stuff ****/
   public Map<String, ExtensionInstruction> extension_instructions ()
   {
      return extension_instructions;
   }

   public Map<String, ExtensionInstruction> extension_first_level_instructions
   (
   )
   {
      return extension_first_level_instructions;
   }

   public Map<String, ExtensionValueNode> extension_value_nodes
   (
   )
   {
      return extension_value_nodes;
   }

   /**** Collections ****/
   public DeclarationCollection<Event> events ()
   {
      return event_collection;
   }

   public DeclarationCollection<Macro> macros ()
   {
      return macro_collection;
   }

   public DeclarationCollection<Sequence> sequences ()
   {
      return sequence_collection;
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

   public void add_global_instruction (final InstructionNode instruction)
   {
      global_instructions.add(instruction);
   }

   public List<InstructionNode> get_global_instructions ()
   {
      return global_instructions;
   }

   /**** Misc. ****************************************************************/
   public boolean assert_sanity ()
   throws UnknownSequenceException
   {
      boolean is_sane;

      is_sane = true;

      is_sane = assert_sane_sequence_calls() & is_sane;

      return is_sane;
   }

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

   protected boolean assert_sane_sequence_calls ()
   throws UnknownSequenceException
   {
      boolean is_sane;

      is_sane = true;

      for
      (
         final Map.Entry<String, List<Origin>> entry:
            sequence_calls.entrySet()
      )
      {
         if (!sequences().has(entry.getKey()))
         {
            final List<Origin> occurrences;

            occurrences = entry.getValue();

            if (occurrences.isEmpty())
            {
               continue;
            }

            is_sane = false;

            ErrorManager.handle
            (
               new UnknownSequenceException
               (
                  occurrences.get(0),
                  entry.getKey(),
                  ((occurrences.size() == 1) ? null : occurrences)
               )
            );
         }
      }

      return is_sane;
   }
}
