package tonkadur;

import java.util.List;

import java.io.IOException;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.parser.ParserData;

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
      final ParserData parser_data;
      final boolean valid_use;

      tonkadur.fate.v1.Base.initialize();
      tonkadur.wyrd.v1.Base.initialize();

      TonkadurPlugin.load_special_classes();

      valid_use = RuntimeParameters.parse_options(args);

      plugins = TonkadurPlugin.get_plugins();

      if (!valid_use)
      {
         RuntimeParameters.print_usage(plugins);

         return;
      }

      for (final TonkadurPlugin tp: plugins)
      {
         tp.initialize(args);
      }

      fate_world = new tonkadur.fate.v1.lang.World();
      parser_data = new ParserData(fate_world);

      for (final TonkadurPlugin tp: plugins)
      {
         tp.pre_fate_parsing(fate_world, parser_data);
      }

      if (RuntimeParameters.get_input_file() == null)
      {
         return;
      }

      try
      {
         parser_data.add_file_content
         (
            Origin.BASE_LANGUAGE,
            RuntimeParameters.get_input_file()
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
