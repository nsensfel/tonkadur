package tonkadur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.jar.JarFile;

import tonkadur.error.ErrorCategory;

public class RuntimeParameters
{
   protected static final String version;
   protected static final List<String> include_directories;
   protected static final Collection<ErrorCategory> disabled_errors;
   protected static final Collection<ErrorCategory> tolerated_errors;
   protected static boolean consider_warnings_as_errors;
   protected static String target_file;

   static
   {
      version = "0.9.2";
      include_directories = new ArrayList<String>();
      disabled_errors = new HashSet<ErrorCategory>();
      tolerated_errors = new HashSet<ErrorCategory>();
      consider_warnings_as_errors = false;
   }

   public static boolean error_is_disabled (final ErrorCategory category)
   {
      return disabled_errors.contains(category);
   }

   public static boolean error_is_tolerated (final ErrorCategory category)
   {
      return tolerated_errors.contains(category);
   }

   public static boolean warnings_are_errors ()
   {
      return consider_warnings_as_errors;
   }

   public static List<String> get_include_directories ()
   {
      return include_directories;
   }

   public static String get_input_file ()
   {
      return target_file;
   }

   public static void add_include_directory (final String name)
   {
      include_directories.add(name);
   }

   public static void print_usage
   (
      final Collection<TonkadurPlugin> plugins
   )
   {
      System.out.println("Tonkadur version " + version);
      System.out.println("Usage: tonkadur [<options>] <file>");
      System.out.println("Options:");
      System.out.println
      (
         " -i|--include <directory>\tAdds <directory> to the search path."
      );
      System.out.println
      (
         " -s|--strict\t\t\tWarnings are errors."
      );
      System.out.println
      (
         " -te|--tolerate-error <name>\tRelegates <name> to a simple warning."
      );
      System.out.println
      (
         " -se|--silence-error <name>\tErrors of type <name> are ignored."
      );
      System.out.println
      (
         " -p|--plugin <jar>\t\tLoads plugin classes from jar file."
      );
      System.out.println
      (
         " --legal \t\t\tPrints the relevant licenses."
      );

      for (final TonkadurPlugin plugin: plugins)
      {
         plugin.print_options();
      }

      System.out.println("");
      System.out.println("Home page: https://tonkadur.of.tacticians.online");
   }

   public static boolean parse_options (final String[] options)
   {
      final Iterator<String> options_it;

      if (options.length == 0)
      {
         return false;
      }

      target_file = null;

      options_it = Arrays.stream(options).iterator();

      while (options_it.hasNext())
      {
         final String option = options_it.next();

         if (option.equals("-i") || option.equals("--include"))
         {
            if (!options_it.hasNext())
            {
               return false;
            }

            include_directories.add(options_it.next());
         }
         else if (option.equals("-s") || option.equals("--strict"))
         {
            consider_warnings_as_errors = true;
         }
         else if (option.equals("-p") || option.equals("--plugin"))
         {
            final String jar_name;

            if (!options_it.hasNext())
            {
               return false;
            }

            jar_name = options_it.next();

            try
            {
               System.out.println("[D] Loading jar '" + jar_name +"'...");

               TonkadurPlugin.load_all_relevant_classes_in
               (
                  new JarFile(jar_name)
               );
            }
            catch (final Throwable t)
            {
               System.err.println
               (
                  "[F] Unable to load plugin jar '" + jar_name + "':"
               );

               t.printStackTrace();

               System.exit(-1);
            }
         }
         else if (option.equals("-te") || option.equals("--tolerate-error"))
         {
            final ErrorCategory er;
            final String er_name;

            if (!options_it.hasNext())
            {
               return false;
            }

            er_name = options_it.next();

            er = ErrorCategory.get_error_category(er_name);

            if (er == null)
            {
               System.err.println("Unknown error type \"" + er_name + "\".");
               System.err.println("Available error types:");


               for (final String er_type: ErrorCategory.get_error_categories())
               {
                  System.err.println("- " + er_type);
               }

               System.err.println("");

               return false;
            }

            tolerated_errors.add(er);
         }
         else if (option.equals("-se") || option.equals("--silence-error"))
         {
            final ErrorCategory er;
            final String er_name;

            if (!options_it.hasNext())
            {
               return false;
            }

            er_name = options_it.next();

            er = ErrorCategory.get_error_category(er_name);

            if (er == null)
            {
               System.err.println("Unknown error type \"" + er_name + "\".");
               System.err.println("Available error types:");


               for (final String er_type: ErrorCategory.get_error_categories())
               {
                  System.err.println("- " + er_type);
               }

               System.err.println("");

               return false;
            }

            disabled_errors.add(er);
         }
         else if (option.equals("--legal"))
         {
            print_license();
         }
         else if (!options_it.hasNext())
         {
            target_file = option;
         }
      }

      return true;
   }

   protected static void print_license ()
   {
      System.out.println("Tonkadur is released under an Apache license. Go to https://tonkadur.of.tacticians.online/LICENSE to consult it.");
      System.out.println("Tonkadur uses and ships with a copy of ANTLR 4. Go to https://www.antlr.org/license.html to see ANTLR's license.");
   }
}
