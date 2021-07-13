package tonkadur.plugin.base;

import java.util.jar.JarFile;

import tonkadur.TonkadurPlugin;

public class BaseLanguage extends TonkadurPlugin
{
   @Override
   public void initialize (final String[] args)
   throws Throwable
   {
      final JarFile base_jar;

      base_jar =
         new JarFile
         (
            BaseLanguage.class.getProtectionDomain
            (
            ).getCodeSource().getLocation().getFile()
         );

      TonkadurPlugin.initialize_classes_in
      (
         base_jar,
         "tonkadur/fate/v1/lang/computation/generic"
      );

      TonkadurPlugin.initialize_classes_in
      (
         base_jar,
         "tonkadur/fate/v1/lang/instruction/generic"
      );

      TonkadurPlugin.initialize_classes_in
      (
         base_jar,
         "tonkadur/wyrd/v1/compiler/fate/v1/computation/generic"
      );

      TonkadurPlugin.initialize_classes_in
      (
         base_jar,
         "tonkadur/wyrd/v1/compiler/fate/v1/instruction/generic"
      );
   }
}
