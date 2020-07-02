package tonkadur;

import java.io.IOException;

import tonkadur.fate.v1.Utils;

public class Main
{
   /* Utility class */
   private Main () {};

   public static void main (final String[] args)
   throws IOException
   {
      Utils.parse_file(args[0]);
   }
}
