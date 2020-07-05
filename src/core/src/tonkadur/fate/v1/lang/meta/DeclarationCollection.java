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
import tonkadur.fate.v1.error.IncomparableDeclarationException;

public class DeclarationCollection <Declared extends DeclaredEntity>
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Map<String, Declared> collection;
   protected final Declared value_on_missing;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/

   /**** Constructors *********************************************************/
   public DeclarationCollection (final Declared value_on_missing)
   {
      collection = new HashMap<String, Declared>();
      this.value_on_missing = value_on_missing;
   }

   /**** Accessors ************************************************************/
   public Collection<Declared> get_all ()
   {
      return collection.values();
   }

   public boolean has (final String name)
   {
      return collection.containsKey(name);
   }

   public void add (Declared entity)
   throws
      DuplicateDeclarationException,
      ConflictingDeclarationException,
      IncompatibleDeclarationException,
      IncomparableDeclarationException
   {
      entity = assert_entity_can_be_added(entity);
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

   /**** Misc. ****************************************************************/
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("Default Value: ");
      sb.append(value_on_missing.toString());
      sb.append(System.lineSeparator());

      for (final Map.Entry<String, Declared> entry: collection.entrySet())
      {
         sb.append("- ");
         sb.append(entry.getKey());
         sb.append(": ");
         sb.append(entry.getValue().toString());
         sb.append(System.lineSeparator());
      }

      return sb.toString();
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected Declared assert_entity_can_be_added (final Declared new_version)
   throws
      DuplicateDeclarationException,
      ConflictingDeclarationException,
      IncompatibleDeclarationException,
      IncomparableDeclarationException
   {
      final DeclaredEntity de;
      final Declared previous_version;
      Declared hint;

      previous_version = collection.get(new_version.get_name());

      if (previous_version == null)
      {
         return new_version;
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
         return new_version;
      }

      ErrorManager.handle
      (
         new ConflictingDeclarationException(new_version, previous_version)
      );

      if (!previous_version.is_incompatible_with_declaration(new_version))
      {
         return new_version;
      }

      de = new_version.generate_comparable_to(previous_version);

      try
      {
         hint = (Declared) de;
      }
      catch (final ClassCastException e)
      {
         hint = null;
         e.printStackTrace();

         System.exit(-1);
      }

      if (new_version.equals(value_on_missing))
      {
         ErrorManager.handle
         (
            new IncompatibleDeclarationException(new_version, previous_version)
         );

         ErrorManager.handle
         (
            new IncomparableDeclarationException(new_version, previous_version)
         );
      }
      else
      {
         ErrorManager.handle
         (
            new IncompatibleDeclarationException
            (
               new_version,
               previous_version,
               hint
            )
         );
      }

      return hint;
   }
}
