package tonkadur.fate.v1.lang.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

import tonkadur.error.ErrorManager;
import tonkadur.error.BasicError;
import tonkadur.error.ErrorLevel;

import tonkadur.parser.Context;
import tonkadur.parser.Location;
import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.ErrorCategory;

import tonkadur.fate.v1.lang.meta.DeclaredEntity;

public class FutureType extends Type
{
   public static final Collection<FutureType> FUTURE_TYPES;

   static
   {
      FUTURE_TYPES = new ArrayList<FutureType>();
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected Type resolved_type;

   protected void assert_is_resolved ()
   {
      if (resolved_type == null)
      {
         try
         {
            ErrorManager.handle
            (
               new BasicError
               (
                  ErrorLevel.FATAL,
                  ErrorCategory.MISSING_DECLARATION,
                  (
                     "Future Type from "
                     + get_origin().toString()
                     + " used prior to resolution."
                  )
               )
            );
         }
         catch (final Throwable t)
         {
            t.printStackTrace();
            System.err.println(t.toString());
            System.exit(-1);
         }
      }
   }

   public static Collection<FutureType> get_unresolved_types ()
   {
      final Collection<FutureType> result;

      result = new ArrayList<FutureType>();

      for (final FutureType t: FUTURE_TYPES)
      {
         if (t.resolved_type == null)
         {
            result.add(t);
         }
      }

      return result;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   public FutureType
   (
      final Origin origin,
      final List<Type> parameters
   )
   {
      super(origin, null, "Future Type", parameters);

      FUTURE_TYPES.add(this);
   }

   public FutureType (final Origin origin)
   {
      super(origin, null, "Future Type", new ArrayList<Type>());

      FUTURE_TYPES.add(this);
   }

   /**** Accessors ************************************************************/
   public Type get_base_type ()
   {
      assert_is_resolved();

      return resolved_type.get_base_type();
   }

   public Type get_act_as_type ()
   {
      assert_is_resolved();

      return resolved_type.get_act_as_type();
   }

   public Type get_current_type ()
   {
      if (resolved_type == null)
      {
         return this;
      }
      else if (resolved_type instanceof FutureType)
      {
         return ((FutureType) resolved_type).get_current_type();
      }

      return resolved_type;
   }

   public boolean is_base_type ()
   {
      assert_is_resolved();

      return resolved_type.is_base_type();
   }

   public Type try_merging_with (final Type t)
   {
      assert_is_resolved();

      return resolved_type.try_merging_with(t);
   }

   /**** Compatibility ********************************************************/
   public boolean can_be_used_as (final Type t)
   {
      assert_is_resolved();

      return resolved_type.can_be_used_as(t);
   }

   @Override
   public DeclaredEntity generate_comparable_to (final DeclaredEntity de)
   {
      assert_is_resolved();

      return resolved_type.generate_comparable_to(de);
   }


   /**** Misc. ****************************************************************/
   @Override
   public Type generate_alias (final Origin origin, final String name)
   {
      // FutureType won't work as a stand in for non-basic types, since
      // the casts performed on such classes won't be allowed.
      final FutureType result;

      result = new FutureType(origin, parameters);

      result.resolve_to(this);

      return result;
   }

   @Override
   public boolean is_incompatible_with_declaration (final DeclaredEntity de)
   {
      assert_is_resolved();

      return resolved_type.is_incompatible_with_declaration(de);
   }

   @Override
   public String toString ()
   {
      if (resolved_type == null)
      {
         return "Unresolved Future Type";
      }
      else
      {
         return resolved_type.toString();
      }
   }

   @Override
   public Type generate_variant
   (
      final Origin origin,
      final List<Type> parameters
   )
   throws Throwable
   {
      assert_is_resolved();

      return resolved_type.generate_variant(origin, parameters);
   }

   public Type get_resolved_type ()
   {
      if
      (
         (resolved_type != null)
         && (resolved_type instanceof FutureType)
      )
      {
         return ((FutureType) resolved_type).get_resolved_type();
      }
      else
      {
         return resolved_type;
      }
   }

   public void resolve_to (final Type t)
   {
      if (resolved_type == null)
      {
         resolved_type = t;
      }
   }

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   @Override
   protected boolean this_or_parent_equals (final Type t)
   {
      assert_is_resolved();

      return resolved_type.this_or_parent_equals(t);
   }

   @Override
   protected List<Type> compute_full_type_chain ()
   {
      final List<Type> result;
      Type t;

      result = new ArrayList<Type>();

      t = this;

      while (t != null)
      {
         result.add(t);

         t = t.parent;
      }

      Collections.reverse(result);

      return result;
   }
}
