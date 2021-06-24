package tonkadur.fate.v1.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tonkadur.functional.Cons;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.UnknownSequenceException;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.DeclarationCollection;
import tonkadur.fate.v1.lang.meta.ExtraInstruction;
import tonkadur.fate.v1.lang.meta.ExtraComputation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;
import tonkadur.fate.v1.lang.meta.Instruction;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.DictionaryType;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.SequenceType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.computation.SequenceReference;

public class World
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Set<String> loaded_files;
   protected final Set<String> required_extensions;

   protected final Map<String, List<Cons<Origin, List<Computation>>>>
      sequence_uses;
   protected final Map<String, List<SequenceReference>> sequence_variables;

   protected final DeclarationCollection<ExtraInstruction> ei_collection;
   protected final DeclarationCollection<ExtraComputation> ec_collection;
   protected final DeclarationCollection<Event> event_collection;
   protected final DeclarationCollection<Sequence> sequence_collection;
   protected final DeclarationCollection<TextEffect> text_effect_collection;
   protected final DeclarationCollection<Type> type_collection;
   protected final DeclarationCollection<Variable> variable_collection;

   protected final List<Instruction> global_instructions;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public World ()
   {
      loaded_files = new HashSet<String>();
      required_extensions = new HashSet<String>();

      sequence_uses =
         new HashMap<String, List<Cons<Origin, List<Computation>>>>();

      sequence_variables = new HashMap<String, List<SequenceReference>>();

      ei_collection =
         new DeclarationCollection<ExtraInstruction>
         (
            ExtraInstruction.value_on_missing()
         );
      ec_collection =
         new DeclarationCollection<ExtraComputation>
         (
            ExtraComputation.value_on_missing()
         );

      event_collection =
         new DeclarationCollection<Event>(Event.value_on_missing());
      sequence_collection = new DeclarationCollection<Sequence>(null);

      text_effect_collection =
         new DeclarationCollection<TextEffect>(TextEffect.value_on_missing());
      type_collection =
         new DeclarationCollection<Type>(Type.value_on_missing());
      variable_collection =
         new DeclarationCollection<Variable>(Variable.value_on_missing());

      add_base_types();

      global_instructions = new ArrayList<Instruction>();
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
   public void add_sequence_use
   (
      final Origin origin,
      final String sequence,
      final List<Computation> parameters
   )
   {
      List<Cons<Origin, List<Computation>>> list_of_uses;

      list_of_uses = sequence_uses.get(sequence);

      if (list_of_uses == null)
      {
         list_of_uses = new ArrayList<Cons<Origin, List<Computation>>>();

         sequence_uses.put(sequence, list_of_uses);
      }

      list_of_uses.add(new Cons(origin, parameters));
   }

   public void add_sequence_variable (final SequenceReference sr)
   {
      List<SequenceReference> list_of_variables;

      list_of_variables = sequence_variables.get(sr.get_sequence_name());

      if (list_of_variables == null)
      {
         list_of_variables = new ArrayList<SequenceReference>();

         sequence_variables.put(sr.get_sequence_name(), list_of_variables);
      }

      list_of_variables.add(sr);
   }

   /**** Collections ****/
   public DeclarationCollection<ExtraInstruction> extra_instructions ()
   {
      return ei_collection;
   }

   public DeclarationCollection<ExtraComputation> extra_computations ()
   {
      return ec_collection;
   }

   public DeclarationCollection<Event> events ()
   {
      return event_collection;
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

   public void add_global_instruction (final Instruction instruction)
   {
      global_instructions.add(instruction);
   }

   public List<Instruction> get_global_instructions ()
   {
      return global_instructions;
   }

   /**** Misc. ****************************************************************/
   public boolean assert_sanity ()
   throws Throwable
   {
      boolean is_sane;

      is_sane = true;

      is_sane = assert_sane_sequence_uses() & is_sane;
      is_sane = assert_sane_sequence_variables() & is_sane;

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
      sb.append("Extra Instructions:");
      sb.append(System.lineSeparator());
      sb.append(ei_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append(System.lineSeparator());
      sb.append("Extra Computations:");
      sb.append(System.lineSeparator());
      sb.append(ec_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append(System.lineSeparator());
      sb.append("Events:");
      sb.append(System.lineSeparator());
      sb.append(event_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Text Effects:");
      sb.append(System.lineSeparator());
      sb.append(text_effect_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Types:");
      sb.append(System.lineSeparator());
      sb.append(type_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Variables:");
      sb.append(System.lineSeparator());
      sb.append(variable_collection.toString());
      sb.append(System.lineSeparator());
      sb.append(System.lineSeparator());

      sb.append("Sequences:");
      sb.append(System.lineSeparator());
      sb.append(sequence_collection.toString());
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
         type_collection.add(Type.BOOL);
         type_collection.add(Type.FLOAT);
         type_collection.add(Type.INT);
         type_collection.add(Type.STRING);
         type_collection.add(Type.TEXT);

         type_collection.add(CollectionType.LIST_ARCHETYPE);
         type_collection.add(CollectionType.SET_ARCHETYPE);

         type_collection.add(ConsType.ARCHETYPE);

         type_collection.add(DictionaryType.ARCHETYPE);

         type_collection.add(LambdaType.ARCHETYPE);
         type_collection.add(SequenceType.ARCHETYPE);

         //type_collection.add(Type.SET);
         //
         //type_collection.add(Type.DICT);
      }
      catch (final Throwable t)
      {
         System.err.println("Unable to add base types:" + t.toString());
         System.exit(-1);
      }
   }

   protected boolean assert_sane_sequence_uses ()
   throws Throwable
   {
      Sequence seq;

      boolean is_sane;

      is_sane = true;

      for
      (
         final Map.Entry<String, List<Cons<Origin, List<Computation>>>> entry:
            sequence_uses.entrySet()
      )
      {
         seq = sequences().get_or_null(entry.getKey());

         if (seq == null)
         {
            final List<Cons<Origin, List<Computation>>> occurrences;

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
                  occurrences.get(0).get_car(),
                  entry.getKey(),
                  ((occurrences.size() == 1) ? null : occurrences)
               )
            );
         }
         else
         {
            for (final Cons<Origin, List<Computation>> use: entry.getValue())
            {
               if
               (
                  !seq.assert_can_take_parameters(use.get_car(), use.get_cdr())
               )
               {
                  is_sane = false;
               }
            }
         }
      }

      return is_sane;
   }

   protected boolean assert_sane_sequence_variables ()
   throws Throwable
   {
      Sequence seq;

      boolean is_sane;

      is_sane = true;

      for
      (
         final Map.Entry<String, List<SequenceReference>> entry:
            sequence_variables.entrySet()
      )
      {
         seq = sequences().get_or_null(entry.getKey());

         if (seq == null)
         {
            final List<SequenceReference> variables;

            variables = entry.getValue();

            if (variables.isEmpty())
            {
               continue;
            }

            is_sane = false;

            ErrorManager.handle
            (
               new UnknownSequenceException
               (
                  entry.getKey(),
                  ((variables.size() == 1) ? null : variables)
               )
            );
         }
         else
         {
            final List<Type> signature_types;

            signature_types = new ArrayList<Type>();

            for (final Variable v: seq.get_signature())
            {
               signature_types.add(v.get_type());
            }

            for (final SequenceReference sr: entry.getValue())
            {
               ((SequenceType) sr.get_type()).propose_signature
               (
                  signature_types
               );

               RecurrentChecks.assert_types_matches_signature
               (
                  sr.get_origin(),
                  ((SequenceType) sr.get_type()).get_signature(),
                  signature_types
               );
            }
         }
      }

      return is_sane;
   }
}
