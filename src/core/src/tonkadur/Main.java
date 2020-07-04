package tonkadur;

import java.io.IOException;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.Utils;

public class Main
{
   /* Utility class */
   private Main () {};

   public static void main (final String[] args)
   throws IOException
   {
      final World world;

      world = new World();

      Utils.add_file_content(args[0], world);
   }
}
