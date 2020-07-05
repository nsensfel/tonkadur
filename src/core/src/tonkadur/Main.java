package tonkadur;

import java.io.IOException;

import tonkadur.parser.Context;

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
      final Context context;

      world = new World();
      context = new Context(args[0]);

      Utils.add_file_content(args[0], context, world);

      System.out.println("Parsing completed.");
      System.out.println(world.toString());
   }
}
