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
   protected static final List<Class> LOADABLE_SUPERCLASSES;
   protected static final List<TonkadurPlugin> TONKADUR_PLUGINS;

   static
   {
      LOADABLE_SUPERCLASSES = new ArrayList<Class>();
      TONKADUR_PLUGINS = new ArrayList<TonkadurPlugin>();

      register_as_loadable_superclass(TonkadurPlugin.class);
   }

   public static void register_as_loadable_superclass (final Class c)
   {
      LOADABLE_SUPERCLASSES.add(c);
   }

   public static void register (final Class c)
   {
      try
      {
         TONKADUR_PLUGINS.add
         (
            (TonkadurPlugin) c.getConstructor().newInstance()
         );
      }
      catch (final Throwable t)
      {
         System.err.println
         (
            "[E] Unable to create an instance of Tonkadur Plugin class '"
            + c.getName()
            + "':"
         );

         t.printStackTrace();
      }
   }

   public static void load_all_relevant_classes_in
   (
      final JarFile current_jar
   )
   {
      final Enumeration<JarEntry> entries;

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
            || candidate_name.startsWith("org/antlr/")
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

         try
         {
            final Class c;

            c =
               Class.forName
               (
                  candidate_name,
                  true,
                  new URLClassLoader
                  (
                     new URL[]{new File(current_jar.getName()).toURI().toURL()},
                     TonkadurPlugin.class.getClassLoader()
                  )
               );

            for (final Class superclass: LOADABLE_SUPERCLASSES)
            {
               if (superclass.isAssignableFrom(c) && !c.equals(superclass))
               {
                  superclass.getDeclaredMethod
                  (
                     "register",
                     Class.class
                  ).invoke(/*object = */ null, c);

                  break;
               }
            }
         }
         catch (final Throwable e)
         {
            System.err.println
            (
               "[E] Could not load class "
               + candidate_name
               + ": "
            );
            e.printStackTrace();
         }
      }
   }

   public static void load_special_classes ()
   {
      System.out.println("[D] Loading special classes from main jar...");

      try
      {
         load_all_relevant_classes_in
         (
            new JarFile
            (
               TonkadurPlugin.class.getProtectionDomain
               (
               ).getCodeSource().getLocation().getFile()
            )
         );
      }
      catch (final Exception e)
      {
         e.printStackTrace();
      }
   }

   public static List<TonkadurPlugin> get_plugins ()
   {
      return TONKADUR_PLUGINS;
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
