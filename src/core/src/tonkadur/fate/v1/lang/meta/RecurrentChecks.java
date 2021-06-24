package tonkadur.fate.v1.lang.meta;

import java.util.Collections;
import java.util.List;

import tonkadur.error.ErrorManager;

import tonkadur.functional.Merge;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.error.ConflictingTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.SignatureTypeMismatchException;
import tonkadur.fate.v1.error.IncorrectReturnTypeException;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.LambdaType;
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
               Collections.singletonList(CollectionType.LIST_ARCHETYPE)
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
               Collections.singletonList(CollectionType.SET_ARCHETYPE)
            )
         );
      }
   }

   public static void assert_is_a_collection (final Computation a)
   throws ParsingError
   {
      assert_is_a_collection(a.get_origin(), a.get_type());
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

   public static void assert_is_a_list_of
   (
      final Computation c,
      final Computation e
   )
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

   public static void assert_is_a_set_of
   (
      final Computation c,
      final Computation e
   )
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

   public static void assert_computations_matches_signature
   (
      final Origin o,
      final List<Computation> c,
      final List<Type> t
   )
   throws ParsingError
   {
      if (c.size() != t.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               o,
               c.size(),
               t.size(),
               t.size()
            )
         );
      }

      try
      {
         (new Merge<Type, Computation, Boolean>()
         {
            @Override
            public Boolean risky_lambda (final Type t, final Computation p)
            throws ParsingError
            {
               assert_can_be_used_as(p, t);

               return Boolean.TRUE;
            }
         }).risky_merge(t, c);
      }
      catch (final ParsingError pe)
      {
         throw pe;
      }
      catch (final Throwable e)
      {
         e.printStackTrace();
         System.exit(-1);
      }
   }

   public static void assert_types_matches_signature
   (
      final Origin o,
      final List<Type> c,
      final List<Type> t
   )
   throws ParsingError
   {
      if (c.size() != t.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               o,
               c.size(),
               t.size(),
               t.size()
            )
         );
      }

      try
      {
         (new Merge<Type, Type, Boolean>()
         {
            @Override
            public Boolean risky_lambda (final Type t, final Type p)
            throws ParsingError
            {
               assert_can_be_used_as(o, p, t);

               return Boolean.TRUE;
            }
         }).risky_merge(t, c);
      }
      catch (final ParsingError e)
      {
         System.err.println(e.toString());

         ErrorManager.handle
         (
            new SignatureTypeMismatchException(o, c, t)
         );
      }
      catch (final Throwable e)
      {
         e.printStackTrace();
         System.exit(-1);
      }
   }

   public static void assert_is_a_lambda_function (final Computation l)
   throws ParsingError
   {
      if (!(l.get_type() instanceof LambdaType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               l.get_origin(),
               l.get_type(),
               Collections.singleton(LambdaType.ARCHETYPE)
            )
         );
      }
   }

   public static void assert_return_type_is
   (
      final Computation l,
      final Type r
   )
   throws ParsingError
   {
      try
      {
         assert_can_be_used_as
         (
            l.get_origin(),
            ((LambdaType) l.get_type()).get_return_type(),
            r
         );
      }
      catch (final ParsingError e)
      {
         System.err.println(e.toString());

         ErrorManager.handle
         (
            new IncorrectReturnTypeException
            (
               l.get_origin(),
               ((LambdaType) l.get_type()).get_return_type(),
               r
            )
         );
      }
   }

   public static void assert_lambda_matches_computations
   (
      final Computation l,
      final Type r,
      final List<Computation> c
   )
   throws ParsingError
   {
      assert_is_a_lambda_function(l);
      assert_return_type_is(l, r);
      assert_computations_matches_signature
      (
         l.get_origin(),
         c,
         ((LambdaType) l.get_type()).get_signature()
      );
   }

   public static void assert_lambda_matches_computations
   (
      final Computation l,
      final List<Computation> c
   )
   throws ParsingError
   {
      assert_is_a_lambda_function(l);
      assert_computations_matches_signature
      (
         l.get_origin(),
         c,
         ((LambdaType) l.get_type()).get_signature()
      );
   }

   public static void assert_lambda_matches_types
   (
      final Computation l,
      final List<Type> c
   )
   throws ParsingError
   {
      assert_is_a_lambda_function(l);
      assert_types_matches_signature
      (
         l.get_origin(),
         c,
         ((LambdaType) l.get_type()).get_signature()
      );
   }

   public static void assert_lambda_matches_types
   (
      final Computation l,
      final Type r,
      final List<Type> c
   )
   throws ParsingError
   {
      assert_is_a_lambda_function(l);
      assert_return_type_is(l, r);
      assert_types_matches_signature
      (
         l.get_origin(),
         c,
         ((LambdaType) l.get_type()).get_signature()
      );
   }
}
