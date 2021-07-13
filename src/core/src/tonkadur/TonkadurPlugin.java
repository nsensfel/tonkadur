package tonkadur;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.URI;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.io.File;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import tonkadur.parser.Context;

public abstract class TonkadurPlugin
{
   public static List<Class> get_classes_in
   (
      final JarFile current_jar,
      final String folder_path
   )
   {
      final List<Class> result;
      final Enumeration<JarEntry> entries;

      result = new ArrayList<Class>();

      entries = current_jar.entries();

      while (entries.hasMoreElements())
      {
         final JarEntry candidate;
         String candidate_name;

         candidate = entries.nextElement();
         candidate_name = candidate.getName();

         if
         (
            !candidate_name.endsWith(".class")
            || !candidate_name.startsWith(folder_path)
         )
         {
            continue;
         }

         candidate_name =
            candidate_name.replace
            (
               '/',
               '.'
            ).substring(0, (candidate_name.length() - 6));

         System.out.println("[D] Loading class " + candidate + "...");

         try
         {
            result.add
            (
               Class.forName
               (
                  candidate_name,
                  true,
                  new URLClassLoader
                  (
                     new URL[]{new File(current_jar.getName()).toURI().toURL()},
                     TonkadurPlugin.class.getClassLoader()
                  )
               )
            );
         }
         catch (final Throwable e)
         {
            System.err.println
            (
               "Could not load class "
               + candidate_name
               + ": "
            );
            e.printStackTrace();
         }
      }

      return result;
   }

   public static void initialize_classes_in
   (
      final JarFile current_jar,
      final String folder_path
   )
   throws Throwable
   {
      // This already initializes the classes.
      get_classes_in(current_jar, folder_path);
   }

   public static List<TonkadurPlugin> extract_plugins_from
   (
      final JarFile current_jar
   )
   {
      final List<TonkadurPlugin> plugins;

      plugins = new ArrayList<TonkadurPlugin>();

      for (final Class c: get_classes_in(current_jar, "tonkadur/plugin"))
      {
         try
         {
            plugins.add
            (
               (TonkadurPlugin) c.newInstance()
            );
         }
         catch (final Throwable e)
         {
            System.err.println
            (
               "Could not load plugin "
               + c.getName()
               + ": "
            );
            e.printStackTrace();
         }
      }

      return plugins;
   }

   public static List<TonkadurPlugin> get_plugins ()
   {
      final List<TonkadurPlugin> plugins;

      plugins = new ArrayList<TonkadurPlugin>();

      try
      {
         plugins.addAll
         (
            extract_plugins_from
            (
               new JarFile
               (
                  TonkadurPlugin.class.getProtectionDomain
                  (
                  ).getCodeSource().getLocation().getFile()
               )
            )
         );
      }
      catch (final Exception e)
      {
         e.printStackTrace();
      }

      for (final String jar_name: RuntimeParameters.get_jar_plugins())
      {
         try
         {
            plugins.addAll
            (
               extract_plugins_from(new JarFile(jar_name))
            );
         }
         catch (final Exception e)
         {
            e.printStackTrace();
         }
      }

      return plugins;
   }

   public void initialize (final String[] args)
   throws Throwable
   {

   }

   public void pre_fate_parsing
   (
      final tonkadur.fate.v1.lang.World fate_world,
      final tonkadur.fate.v1.parser.ParserData parser_data
   )
   throws Throwable
   {

   }

   public void post_fate_parsing (final tonkadur.fate.v1.lang.World fate_world)
   throws Throwable
   {

   }

   public void pre_wyrd_compile (final tonkadur.wyrd.v1.lang.World wyrd_world)
   throws Throwable
   {

   }

   public void post_wyrd_compile (final tonkadur.wyrd.v1.lang.World wyrd_world)
   throws Throwable
   {

   }

   public void finalize ()
   throws Throwable
   {

   }

   public void print_options ()
   {
   }
}
