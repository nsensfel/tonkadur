package tonkadur.plugin;

import tonkadur.TonkadurPlugin;

import tonkadur.wyrd.v1.lang.World;

import tonkadur.jsonexport.Translator;

public class JSONExport extends TonkadurPlugin
{
   World wyrd_world;
   String output_file;

   @Override
   public void initialize (final String[] args)
   throws Throwable
   {
      output_file = (args[0] + ".json");
   }

   @Override
   public void post_wyrd_compile (final World wyrd_world)
   throws Throwable
   {
      this.wyrd_world = wyrd_world;
   }

   @Override
   public void finalize ()
   throws Throwable
   {
      Translator.toJSON(wyrd_world, output_file);
   }
}
