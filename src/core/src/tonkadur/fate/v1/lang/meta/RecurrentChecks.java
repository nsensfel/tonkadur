package tonkadur.fate.v1.lang.meta;

import java.util.Collections;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.Type;

public class RecurrentChecks
{
   /* Utility Class */
   private RecurrentChecks () { }

   public static Type assert_can_be_used_as
   (
      final Computation a,
      final Computation b
   )
   throws ParsingError
   {
      return assert_can_be_used_as(a.get_origin(), a.get_type(), b.get_type());
   }

   public static Type assert_can_be_used_as
   (
      final Computation a,
      final Type t
   )
   throws ParsingError
   {
      return assert_can_be_used_as(a.get_origin(), a.get_type(), t);
   }

   public static Type assert_can_be_used_as
   (
      final Origin o,
      final Type a,
      final Type b
   )
   throws ParsingError
   {
      Type result;

      if (a.can_be_used_as(b))
      {
         return b;
      }

      result = a.try_merging_with(b);

      if (result != null)
      {
         return result;
      }

      ErrorManager.handle(new ConflictingTypeException(o, a, b));

      result = (Type) a.generate_comparable_to(b);

      if (result.equals(Type.ANY))
      {
         ErrorManager.handle(new IncomparableTypeException(o, a, b));
      }

      return result;
   }

   public static void assert_is_a_list (final Computation a)
   throws ParsingError
   {
      assert_is_a_list(a.get_origin(), a.get_type());
   }

   public static void assert_is_a_list (final Origin o, final Type t)
   throws ParsingError
   {
      if
      (
         (!(t instanceof CollectionType))
         || ((CollectionType) t).is_set()
      )
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               o,
               t,
               Collections.singletonList(Type.LIST)
            )
         );
      }
   }

   public static void assert_is_a_set (final Computation a)
   throws ParsingError
   {
      assert_is_a_set(a.get_origin(), a.get_type());
   }

   public static void assert_is_a_set (final Origin o, final Type t)
   throws ParsingError
   {
      if
      (
         (!(t instanceof CollectionType))
         || !((CollectionType) t).is_set()
      )
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               o,
               t,
               Collections.singletonList(Type.SET)
            )
         );
      }
   }

   public static void assert_is_a_collection (final Computation a)
   throws ParsingError
   {
      assert_is_a_set(a.get_origin(), a.get_type());
   }

   public static void assert_is_a_collection (final Origin o, final Type t)
   throws ParsingError
   {
      if
      (
         (!(t instanceof CollectionType))
      )
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               o,
               t,
               Type.COLLECTION_TYPES
            )
         );
      }
   }

   public static void assert_is_a_list_of (final Computation c, final Computation e)
   throws ParsingError
   {
      assert_is_a_list_of
      (
         c.get_origin(),
         c.get_type(),
         e.get_origin(),
         e.get_type()
      );
   }

   public static void assert_is_a_list_of
   (
      final Origin oc,
      final Type c,
      final Origin oe,
      final Type e
   )
   throws ParsingError
   {
      assert_is_a_list(oc, c);
      assert_can_be_used_as
      (
         oe,
         e,
         ((CollectionType) c).get_content_type()
      );
   }

   public static void assert_is_a_set_of (final Computation c, final Computation e)
   throws ParsingError
   {
      assert_is_a_set_of
      (
         c.get_origin(),
         c.get_type(),
         e.get_origin(),
         e.get_type()
      );
   }

   public static void assert_is_a_set_of
   (
      final Origin oc,
      final Type c,
      final Origin oe,
      final Type e
   )
   throws ParsingError
   {
      assert_is_a_set(oc, c);
      assert_can_be_used_as
      (
         oe,
         e,
         ((CollectionType) c).get_content_type()
      );
   }

   public static void assert_is_a_collection_of
   (
      final Computation c,
      final Computation e
   )
   throws ParsingError
   {
      assert_is_a_collection_of
      (
         c.get_origin(),
         c.get_type(),
         e.get_origin(),
         e.get_type()
      );
   }

   public static void assert_is_a_collection_of
   (
      final Origin oc,
      final Type c,
      final Origin oe,
      final Type e
   )
   throws ParsingError
   {
      assert_is_a_collection(oc, c);
      assert_can_be_used_as
      (
         oe,
         e,
         ((CollectionType) c).get_content_type()
      );
   }
}
