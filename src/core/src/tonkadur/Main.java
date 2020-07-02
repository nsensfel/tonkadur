package tonkadur;

import java.io.IOException;

import tonkadur.parser.Fate;

public class Main
{
   /* Utility class */
   private Main () {};

   public static void main (final String[] args)
   throws IOException
   {
      Fate.parse_file(args[0]);
   }
}
