package tonkadur;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import tonkadur.parser.Context;

public abstract class TonkadurPlugin
{
   public static List<TonkadurPlugin> get_plugins ()
   {
      final List<TonkadurPlugin> plugins;
      final Enumeration<JarEntry> entries;
      JarFile current_jar;

      plugins = new ArrayList<TonkadurPlugin>();

      current_jar = null;

      try
      {
         current_jar =
            new JarFile
            (
               TonkadurPlugin.class.getProtectionDomain
               (
               ).getCodeSource().getLocation().getFile()
            );
      }
      catch (final Exception e)
      {
         e.printStackTrace();
      }

      if (current_jar == null)
      {
         return null;
      }
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
            || !candidate_name.startsWith("tonkadur/plugin/")
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
            plugins.add
            (
               (TonkadurPlugin) Class.forName(candidate_name).newInstance()
            );
         }
         catch (final Throwable e)
         {
            System.err.println
            (
               "Could not load plugin "
               + candidate_name
               + ": "
            );
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
      final Context context
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
}
