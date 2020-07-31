package tonkadur.wyrd.v1.compiler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.wyrd.v1.lang.World;

import tonkadur.wyrd.v1.lang.computation.Constant;

import tonkadur.wyrd.v1.lang.meta.Instruction;

import tonkadur.wyrd.v1.lang.type.Type;

public class InstructionManager
{
   protected Map<String, Integer> label_locations;
   protected Map<String, List<Label>> unresolved_labels;
   protected int generated_labels;

   public InstructionManager ()
   {
      label_locations = new HashMap<String, Integer>();
      unresolved_labels = new HashMap<String, List<Label>>();
      generated_labels = 0;
   }

   public void add_fixed_name_label (final String name)
   {
      if (!unresolved_labels.containsKey(name))
      {
         unresolved_labels.put(name, new ArrayList<Label>());
      }
   }

   public String generate_label ()
   {
      final String result;

      result = "(label " + (generated_labels++) + ")";

      unresolved_labels.put(result, new ArrayList<Label>());

      return result;
   }

   public String generate_label (final String extra)
   {
      final String result;

      result = "(label " + (generated_labels++) + ": " + extra + ")";

      unresolved_labels.put(result, new ArrayList<Label>());

      return result;
   }

   public Constant get_label_constant (final String name)
   {
      final Integer location;

      location = label_locations.get(name);

      if (location == null)
      {
         final Label result;

         result = new Label(name);

         unresolved_labels.get(name).add(result);

         return result;
      }
      else
      {
         return new Constant(Type.INT, Integer.toString(location));
      }
   }

   public List<String> get_unresolved_labels ()
   {
      final ArrayList<String> result;

      result = new ArrayList<String>();

      for (final Map.Entry<String, Integer> entry: label_locations.entrySet())
      {
         if (entry.getValue() == null)
         {
            result.add(entry.getKey());
         }
      }

      return result;
   }

   public void handle_adding_instruction
   (
      final Instruction i,
      final World wyrd_world
   )
   {
      if (i instanceof InstructionList)
      {
         final List<Instruction> instructions;

         instructions = ((InstructionList) i).get_list();

         for (final Instruction instruction: instructions)
         {
            handle_adding_instruction(instruction, wyrd_world);
         }
      }
      else if (i instanceof MarkLabel)
      {
         final MarkLabel mark_label;

         mark_label = (MarkLabel) i;

         if (mark_label.should_mark_after())
         {
            handle_adding_instruction(mark_label.get_instruction(), wyrd_world);

            register_label
            (
               mark_label.get_label_name(),
               wyrd_world.get_current_line()
            );
         }
         else
         {
            register_label
            (
               mark_label.get_label_name(),
               wyrd_world.get_current_line()
            );

            handle_adding_instruction(mark_label.get_instruction(), wyrd_world);
         }
      }
      else
      {
         wyrd_world.add_instruction(i);
      }
   }

   public Instruction mark (final String name, final Instruction i)
   {
      return new MarkLabel(name, false, i);
   }

   public Instruction mark_after (final String name, final Instruction i)
   {
      return new MarkLabel(name, true, i);
   }

   public Instruction mark_after (final Instruction i, final String name)
   {
      return new MarkLabel(name, true, i);
   }

   public Instruction merge (final List<Instruction> instructions)
   {
      if (instructions.isEmpty())
      {
         /* Important in case of label on InstructionList */
         return NOP.generate(this);
      }
      else if (instructions.size() == 1)
      {
         return instructions.get(0);
      }

      return new InstructionList(instructions);
   }

   protected void register_label (final String name, final Integer location)
   {
      if (label_locations.containsKey(name))
      {
         System.err.println("[P] Multiple locations for label '" + name + "'");
      }

      label_locations.put(name, location);

      for (final Label label: unresolved_labels.get(name))
      {
         label.resolve_to(location);
      }

      unresolved_labels.remove(name);
   }

   protected static class InstructionList extends Instruction
   {
      protected final List<Instruction> content;

      public InstructionList (final List<Instruction> content)
      {
         this.content = content;
      }

      public List<Instruction> get_list ()
      {
         return content;
      }
   }

   protected static class MarkLabel extends Instruction
   {
      protected final String label_name;
      protected final boolean should_mark_after;
      protected final Instruction instruction;

      public MarkLabel
      (
         final String label_name,
         final boolean should_mark_after,
         final Instruction instruction
      )
      {
         this.label_name = label_name;
         this.should_mark_after = should_mark_after;
         this.instruction = instruction;
      }

      public String get_label_name ()
      {
         return label_name;
      }

      public boolean should_mark_after ()
      {
         return should_mark_after;
      }

      public Instruction get_instruction ()
      {
         return instruction;
      }
   }

   protected static class Label extends Constant
   {
      protected String changeable_value;

      public Label (final String name)
      {
         super(Type.INT, name);
      }

      @Override
      public String get_as_string ()
      {
         return changeable_value;
      }

      public void resolve_to (final Integer line)
      {
         changeable_value = line.toString();
      }
   }
}
