package tonkadur;

import java.util.List;

import java.io.IOException;

import tonkadur.parser.Context;

import tonkadur.fate.v1.Utils;

public class Main
{
   /* Utility class */
   private Main () {};

   public static void main (final String[] args)
   throws Throwable
   {
      final List<TonkadurPlugin> plugins;
      final tonkadur.fate.v1.lang.World fate_world;
      final tonkadur.wyrd.v1.lang.World wyrd_world;
      final Context context;

      plugins = TonkadurPlugin.get_plugins();

      for (final TonkadurPlugin tp: plugins)
      {
         tp.initialize(args);
      }

      fate_world = new tonkadur.fate.v1.lang.World();
      context = Context.build(args[0]);

      for (final TonkadurPlugin tp: plugins)
      {
         tp.pre_fate_parsing(fate_world, context);
      }

      try
      {
         Utils.add_file_content
         (
            context.get_current_file(),
            context,
            fate_world
         );

         System.out.println("Parsing completed.");
      }
      catch (final Exception e)
      {
         System.err.println("Parsing failed.");
         System.err.println(fate_world.toString());
         e.printStackTrace();

         return;
      }

      for (final TonkadurPlugin tp: plugins)
      {
         tp.post_fate_parsing(fate_world);
      }

      fate_world.assert_sanity();

      wyrd_world = new tonkadur.wyrd.v1.lang.World();

      for (final TonkadurPlugin tp: plugins)
      {
         tp.pre_wyrd_compile(wyrd_world);
      }

      try
      {
         tonkadur.wyrd.v1.compiler.fate.v1.Compiler.compile
         (
            fate_world,
            wyrd_world
         );

         System.out.println("Compilation completed.");
      }
      catch (final Throwable e)
      {
         System.err.println("Compilation failed.");
         e.printStackTrace();
         return;
      }

      for (final TonkadurPlugin tp: plugins)
      {
         tp.post_wyrd_compile(wyrd_world);
      }

      for (final TonkadurPlugin tp: plugins)
      {
         tp.finalize();
      }
   }
}
