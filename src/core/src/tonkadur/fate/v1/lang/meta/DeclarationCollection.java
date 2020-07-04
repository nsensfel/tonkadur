package tonkadur.fate.v1.lang.meta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.MissingDeclarationException;
import tonkadur.fate.v1.error.DuplicateDeclarationException;
import tonkadur.fate.v1.error.ConflictingDeclarationException;
import tonkadur.fate.v1.error.IncompatibleDeclarationException;

public class DeclarationCollection <Declared extends DeclaredEntity>
{
   protected final Map<String, Declared> collection;
   protected final Declared value_on_missing;

   public DeclarationCollection (final Declared value_on_missing)
   {
      collection = new HashMap<String, Declared>();
      this.value_on_missing = value_on_missing;
   }

   public Collection<Declared> get_all ()
   {
      return collection.values();
   }

   public boolean has (final String name)
   {
      return collection.containsKey(name);
   }

   public void add (final Declared entity)
   throws
      DuplicateDeclarationException,
      ConflictingDeclarationException,
      IncompatibleDeclarationException
   {
      assert_entity_can_be_added(entity);
      collection.put(entity.get_name(), entity);
   }

   public Declared get (final Origin call_origin, final String name)
   throws MissingDeclarationException
   {
      final Declared result;

      result = collection.get(name);

      if (result == null)
      {
         ErrorManager.handle
         (
            new MissingDeclarationException
            (
               call_origin,
               value_on_missing.get_type_name(),
               name
            )
         );

         return value_on_missing;
      }

      return result;
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected void assert_entity_can_be_added (final Declared new_version)
   throws
      DuplicateDeclarationException,
      ConflictingDeclarationException,
      IncompatibleDeclarationException
   {
      final Declared previous_version;

      previous_version = collection.get(new_version.get_name());

      if (previous_version == null)
      {
         return;
      }

      ErrorManager.handle
      (
         new DuplicateDeclarationException
         (
            new_version.get_origin(),
            previous_version
         )
      );

      if (!previous_version.conflicts_with_declaration(new_version))
      {
         return;
      }

      ErrorManager.handle
      (
         new ConflictingDeclarationException(new_version, previous_version)
      );

      if (!previous_version.is_incompatible_with_declaration(new_version))
      {
         return;
      }

      ErrorManager.handle
      (
         new IncompatibleDeclarationException(new_version, previous_version)
      );
   }
}
