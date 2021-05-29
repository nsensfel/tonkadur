package tonkadur.fate.v1.parser;

import java.io.IOException;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.Variable;
import tonkadur.fate.v1.lang.World;

public class Parser
{
   final World world;
   final Context context;

   LocalVariables local_variables;

   int breakable_levels;
   int continue_levels;

   public Parser (final World world)
   {
      this.world = world;

      local_variables = new LocalVariables();

      breakable_levels = 0;
      continue_levels = 0;

      context = new Context();
   }

   public Origin get_origin_at (final int line, final int column)
   {
      return context.get_origin_at(line, column);
   }

   public World get_world ()
   {
      return world;
   }

   public Context get_context ()
   {
      return context;
   }

   /**** BREAKABLE LEVELS *****************************************************/
   public boolean can_use_break ()
   {
      return (breakable_levels > 0);
   }

   public void increment_breakable_levels ()
   {
      breakable_levels++;
   }

   public void decrement_breakable_levels ()
   {
      breakable_levels--;
   }

   /**** CONTINUE LEVELS ******************************************************/
   public boolean can_use_continue ()
   {
      return (continue_levels > 0);
   }

   public void increment_continue_levels ()
   {
      continue_levels++;
   }

   public void decrement_continue_levels ()
   {
      continue_levels--;
   }

   /**** LOCAL VARIABLES ******************************************************/
   public Variable maybe_get_local_variable (final String name)
   {
      for (final Map<String, Variable> level: context_variables)
      {
         final Variable v;

         v = level.get(name);

         if (v != null)
         {
            return v;
         }
      }

      return null;
   }

   public void add_local_variables (final Map<String, Variable> collection)
   throws Trowable
   {
      for (final Variable variable: collection.values())
      {
         add_local_variable(variable);
      }
   }

   public void add_local_variable (final Variable variable)
   throws Trowable
   {
      final Map<String, Variable> current_hierarchy = local_variables.peek();
      final Variable previous_entry;

      previous_entry = current_hierarchy.get(variable.get_name());

      if (previous_entry != null)
      {
         ErrorManager.handle
         (
            new DuplicateLocalVariableException
            (
               previous_entry,
               variable
            )
         );
      }

      current_hierarchy.put(variable.get_name(), variable);
   }

   public void increase_local_variables_hierarchy ()
   {
      local_variables.push(new HashMap<String, Variable>());
   }

   public void decrease_local_variables_hierarchy ()
   {
      local_variables.pop();
   }

   /* I don't think it's needed ATM. */
   public Collection<Variable> get_local_variables_at_current_hierarchy ()
   {
      return local_variables.peek().values();
   }

   public LocalVariables get_local_variables_stack ()
   {
      return local_variables;
   }

   public void restore_local_variables_stack
   (
      final LocalVariables local_variables
   )
   {
      this.local_variables = local_variables;
   }

   public void discard_local_variables_stack ()
   {
      local_variables = new LocalVariables();
   }

   /**** ERROR HANDLING *******************************************************/
   public void handle_error (final Throwable e)
   {
      if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
      {
         kthrow
            new ParseCancellationException
            (
               (
                  context.toString()
                  + ((e.getMessage() == null) ? "" : e.getMessage())
               ),
               e
            );
      }
      else
      {
         throw new ParseCancellationException(e);
      }
   }

   public void add_file_content (final Origin origin, final String filename)
   throws IOException
   {
      final CommonTokenStream tokens;
      final TinyFateLexer lexer;
      final TinyFateParser parser;

      lexer = new TinyFateLexer(CharStreams.fromFileName(filename));
      tokens = new CommonTokenStream(lexer);
      parser = new TinyFateParser(tokens);

      if (origin != null)
      {
         context.push(origin);
      }

      parser.fate_file(this);

      if (origin != null)
      {
         context.pop();
      }

      world.add_loaded_file(filename);

      if (parser.getNumberOfSyntaxErrors() > 0)
      {
         throw new IOException("There were syntaxic errors in " + filename);
      }
   }

   public static class LocalVariables extends Deque<Map<String, Variable>>
   {
   }
}
