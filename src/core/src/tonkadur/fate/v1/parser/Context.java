package tonkadur.fate.v1.parser;

import java.util.Stack;

import tonkadur.fate.v1.error.ContextCycleException;

public class Context
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final boolean locked;
   protected final Stack<Location> source;
   protected String current_file;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public Context (final String filename)
   {
      locked = false;
      source = new Stack<Location>();
      current_file = filename;
   }

   /**** Accessors ************************************************************/
   public void push (final Location location, final String new_file)
   throws ContextCycleException
   {
      throw_exception_on_cycle(new_file);

      current_file = new_file;

      source.push(location);
   }

   public void pop ()
   {
      current_file = source.peek().get_filename();
      source.pop();
   }

   /**** Utils ****************************************************************/
   public Origin get_origin_at (final int line, final int column)
   {
      return new Origin(this, new Location(current_file, line, column));
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

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected void throw_exception_on_cycle (final String new_file)
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
            throw new ContextCycleException(previous_import, new_file);
         }

         previous_import = location;
      }

      if (current_file.equals(new_file))
      {
         throw new ContextCycleException(previous_import, new_file);
      }
   }
}
