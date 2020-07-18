package tonkadur;

import java.nio.file.Path;
import java.nio.file.Paths;

import tonkadur.parser.Context;

public class Files
{
   /* Utility class. */
   private Files () {}

   public static String prepare_filename
   (
      final String filename
   )
   {
      return Paths.get(filename).toAbsolutePath().normalize().toString();
   }

   public static String resolve_filename
   (
      final Context context,
      final String name
   )
   {
      Path candidate;

      candidate = Paths.get(context.get_current_directory().toString(), name);

      if (java.nio.file.Files.exists(candidate))
      {
         return candidate.toAbsolutePath().normalize().toString();
      }

      for (final String dir: RuntimeParameters.get_include_directories())
      {
         candidate = Paths.get(dir, name);

         if (java.nio.file.Files.exists(candidate))
         {
            return candidate.toAbsolutePath().normalize().toString();
         }
      }

      candidate = Paths.get(name);

      if (java.nio.file.Files.exists(candidate))
      {
         return candidate.toAbsolutePath().normalize().toString();
      }

      return name;
   }
}
