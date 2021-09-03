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
import tonkadur.fate.v1.lang.type.SequenceType;
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

   public static void assert_can_be_used_in_set
   (
      final Origin o,
      final Type t
   )
   throws ParsingError
   {
      if (!Type.COMPARABLE_TYPES.contains(t.get_act_as_type()))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               o,
               t,
               Type.COMPARABLE_TYPES
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

   public static void propagate_expected_types_and_assert_is_a_list_of
   (
      final Computation c,
      final Computation e
   )
   throws ParsingError
   {
      final Type content_type;

      c.expect_non_string();

      assert_is_a_list(c);

      content_type = ((CollectionType) c.get_type()).get_content_type();

      handle_expected_type_propagation(e, content_type);

      assert_can_be_used_as(e, content_type);
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

   public static void propagate_expected_types_and_assert_is_a_set_of
   (
      final Computation c,
      final Computation e
   )
   throws ParsingError
   {
      final Type content_type;

      c.expect_non_string();

      assert_is_a_set(c);

      content_type = ((CollectionType) c.get_type()).get_content_type();

      handle_expected_type_propagation(e, content_type);

      assert_can_be_used_as(e, content_type);
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

   public static void assert_is_a_set_of (final Computation c, final Type e)
   throws ParsingError
   {
      assert_can_be_used_as
      (
         c,
         CollectionType.build
         (
            c.get_origin(),
            e,
            true,
            "RecurrentChecks is_a_set_of test type"
         )
      );
   }

   public static void assert_is_a_list_of (final Computation c, final Type e)
   throws ParsingError
   {
      assert_can_be_used_as
      (
         c,
         CollectionType.build
         (
            c.get_origin(),
            e,
            false,
            "RecurrentChecks is_a_set_of test type"
         )
      );
   }

   public static void propagate_expected_types_and_assert_is_a_collection_of
   (
      final Computation c,
      final Computation e
   )
   throws ParsingError
   {
      final Type content_type;

      c.expect_non_string();

      assert_is_a_collection(c);

      content_type = ((CollectionType) c.get_type()).get_content_type();

      handle_expected_type_propagation(e, content_type);

      assert_can_be_used_as(e, content_type);
   }

   public static void propagate_expected_types_and_assert_is_a_collection_of
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

   public static void
   propagate_expected_types_and_assert_computations_matches_signature
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
               handle_expected_type_propagation(p, t);
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

   public static void propagate_expected_types_and_assert_is_sequence
   (
      final Computation sequence,
      final List<Computation> params
   )
   throws ParsingError
   {
      final List<Type> sequence_signature;
      final int params_size;

      sequence.expect_non_string();

      assert_is_a_sequence(sequence);

      sequence_signature = ((SequenceType) sequence.get_type()).get_signature();

      params_size = params.size();

      if (params_size != sequence_signature.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               sequence.get_origin(),
               params_size,
               sequence_signature.size(),
               sequence_signature.size()
            )
         );
      }

      for (int i = 0; i < params_size; ++i)
      {
         handle_expected_type_propagation
         (
            params.get(i),
            sequence_signature.get(i)
         );

         assert_can_be_used_as
         (
            params.get(i),
            sequence_signature.get(i)
         );
      }
   }

   public static void propagate_expected_types_and_assert_is_lambda
   (
      final Computation lambda,
      final List<Type> filled_types,
      final List<Computation> extra_params
   )
   throws ParsingError
   {
      final List<Type> lambda_signature;
      final int extra_params_size;

      lambda.expect_non_string();

      assert_is_a_lambda_function(lambda);

      lambda_signature = ((LambdaType) lambda.get_type()).get_signature();

      extra_params_size = extra_params.size();

      if ((filled_types.size() + extra_params_size) != lambda_signature.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               lambda.get_origin(),
               (filled_types.size() + extra_params_size),
               lambda_signature.size(),
               lambda_signature.size()
            )
         );
      }

      for (int i = filled_types.size(), j = 0; i < extra_params_size; ++i, ++j)
      {
         handle_expected_type_propagation
         (
            extra_params.get(j),
            lambda_signature.get(i)
         );

         assert_can_be_used_as
         (
            extra_params.get(j),
            lambda_signature.get(i)
         );
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

   public static void handle_expected_type_propagation
   (
      final Computation computation,
      final Type type
   )
   throws ParsingError
   {
      if
      (
         type.can_be_used_as(Type.TEXT)
         || type.can_be_used_as(Type.STRING)
      )
      {
         computation.expect_string();
      }
      else
      {
         computation.expect_non_string();
      }
   }

   public static void propagate_expected_types
   (
      final List<Computation> computations,
      final List<Type> types
   )
   throws ParsingError
   {
      try
      {
         (new Merge<Computation, Type, Boolean>()
         {
            @Override
            public Boolean risky_lambda (final Computation c, final Type p)
            throws ParsingError
            {
               handle_expected_type_propagation(c, p);

               return Boolean.TRUE;
            }
         }).risky_merge(computations, types);
      }
      catch (final ParsingError e)
      {
         throw e;
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

   public static void assert_is_a_sequence (final Computation l)
   throws ParsingError
   {
      if (!(l.get_type() instanceof SequenceType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               l.get_origin(),
               l.get_type(),
               Collections.singleton(SequenceType.ARCHETYPE)
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

   public static void
   propagate_expected_types_and_assert_lambda_matches_computations
   (
      final Computation l,
      final Type r,
      final List<Computation> c
   )
   throws ParsingError
   {
      l.expect_non_string();

      assert_is_a_lambda_function(l);
      assert_return_type_is(l, r);
      propagate_expected_types_and_assert_computations_matches_signature
      (
         l.get_origin(),
         c,
         ((LambdaType) l.get_type()).get_signature()
      );
   }

   public static void
   propagate_expected_types_and_assert_lambda_matches_computations
   (
      final Computation l,
      final List<Computation> c
   )
   throws ParsingError
   {
      l.expect_non_string();

      assert_is_a_lambda_function(l);
      propagate_expected_types_and_assert_computations_matches_signature
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
