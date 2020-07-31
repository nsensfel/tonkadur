package tonkadur;

import java.io.IOException;

import tonkadur.parser.Context;

import tonkadur.fate.v1.Utils;

public class Main
{
   /* Utility class */
   private Main () {};

   public static void main (final String[] args)
   {
      final tonkadur.fate.v1.lang.World fate_world;
      tonkadur.wyrd.v1.lang.World wyrd_world;
      final Context context;

      fate_world = new tonkadur.fate.v1.lang.World();
      context = Context.build(args[0]);

      try
      {
         Utils.add_file_content
         (
            context.get_current_file(),
            context,
            fate_world
         );

         System.out.println("Parsing completed.");
         System.out.println(fate_world.toString());
      }
      catch (final Exception e)
      {
         System.err.println("Parsing failed.");
         System.err.println(fate_world.toString());
         e.printStackTrace();
      }

      wyrd_world = null;
      try
      {
         wyrd_world =
            tonkadur.wyrd.v1.compiler.fate.v1.Compiler.compile(fate_world);

         System.out.println("Compilation completed.");
      }
      catch (final Throwable e)
      {
         System.err.println("Compilation failed.");
         e.printStackTrace();
      }

      for
      (
         final tonkadur.wyrd.v1.lang.meta.Instruction line:
            wyrd_world.get_code()
      )
      {
         System.out.println(line.toString());
      }
   }
}
