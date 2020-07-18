package tonkadur.parser;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import java.util.Stack;

public class Context
{
   public static Context BASE_LANGUAGE;

   static
   {
      BASE_LANGUAGE = new Context(null, "<Base Language>");
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Stack<Location> source;
   protected Path current_directory;
   protected String current_file;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Context build (String filename)
   {
      Path current_directory;

      current_directory = Paths.get(filename).toAbsolutePath().normalize();
      filename = current_directory.toString();
      current_directory = current_directory.getParent();

      return new Context(current_directory, filename);
   }

   /**** Accessors ************************************************************/
   public void push (final Location location, final String new_file)
   throws
     ContextCycleException
   {
      throw_exception_on_cycle(location, new_file);

      current_file = new_file;
      current_directory = Paths.get(new_file).getParent();

      source.push(location);
   }

   public void pop ()
   {
      final Location previous_location;

      previous_location = source.pop();

      current_file = previous_location.get_filename();
      current_directory = previous_location.get_directory();
   }

   public String get_current_file ()
   {
      return current_file;
   }

   public Path get_current_directory ()
   {
      return current_directory;
   }

   /**** Utils ****************************************************************/
   public Origin get_origin_at (final int line, final int column)
   {
      return
         new Origin
         (
            clone(),
            new Location
            (
               current_directory,
               current_file,
               line,
               column
            )
         );
   }

   public Location get_location_at (final int line, final int column)
   {
      return new Location(current_directory, current_file, line, column);
   }

   public Origin get_origin_at (final Location location)
   {
      return new Origin(clone(), location);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      /*
       * That's in FIFO order, as we want it to be, due to arguable design
       * decisions in Java.
       */
      for (final Location location: source)
      {
         sb.append("Require at ");
         sb.append(location.toString());
         sb.append(" led to");
         sb.append(System.lineSeparator());
      }

      return sb.toString();
   }

   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof Context)
      {
         final Context b;

         b = (Context) o;

         return
            (
               (source.equals(b.source))
               && (current_file.equals (b.current_file))
            );
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return (source.hashCode() + current_file.hashCode());
   }

   @Override
   public Context clone ()
   {
      final Context result;

      result = new Context(current_directory, current_file);

      /*
       * That's in FIFO order, as we want it to be, due to arguable design
       * decisions in Java.
       */
      for (final Location location: source)
      {
         result.source.push(location);
      }

      return result;
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Context (final Path directory, final String filename)
   {
      source = new Stack<Location>();
      current_file = filename;
      current_directory = directory;
   }

   /**** Utils ****************************************************************/
   protected void throw_exception_on_cycle
   (
      final Location declared_at,
      final String new_file
   )
   throws ContextCycleException
   {
      Location previous_import;

      previous_import = null;

      /*
       * That's in FIFO order, as we want it to be, due to arguable design
       * decisions in Java.
       */
      for (final Location location: source)
      {
         if (location.get_filename().equals(new_file))
         {
            throw
               new ContextCycleException
               (
                  get_origin_at(declared_at),
                  previous_import,
                  new_file
               );
         }

         previous_import = location;
      }

      if (current_file.equals(new_file))
      {
         throw
            new ContextCycleException
            (
               get_origin_at(declared_at),
               previous_import,
               new_file
            );
      }
   }
}
