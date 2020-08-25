package tonkadur.plugin;

import java.util.Arrays;
import java.util.Iterator;

import tonkadur.TonkadurPlugin;
import tonkadur.RuntimeParameters;

import tonkadur.wyrd.v1.lang.World;

import tonkadur.jsonexport.Translator;

public class JSONExport extends TonkadurPlugin
{
   World wyrd_world;
   String output_file;

   @Override
   public void initialize (final String[] args)
   throws Throwable
   {
      final Iterator<String> args_it;

      args_it = Arrays.stream(args).iterator();

      output_file = null;

      while (args_it.hasNext())
      {
         final String option = args_it.next();

         if (option.equals("-o") || option.equals("--output"))
         {
            if (!args_it.hasNext())
            {
               throw
                  new Exception
                  (
                     "Invalide usage. No arguments to "
                     + option
                     + " parameter"
                  );
            }

            output_file = args_it.next();

            break;
         }
      }

      if (output_file == null)
      {
         output_file = (RuntimeParameters.get_input_file() + ".json");
      }
   }

   @Override
   public void post_wyrd_compile (final World wyrd_world)
   throws Throwable
   {
      this.wyrd_world = wyrd_world;
   }

   @Override
   public void finalize ()
   throws Throwable
   {
      Translator.toJSON(wyrd_world, output_file);
   }

   @Override
   public void print_options ()
   {
      System.out.println(" -o|--output <file>\t\tOutput to <file>.");
   }
}
