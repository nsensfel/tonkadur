package tonkadur.fate.v1.lang.computation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class Fold extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference lambda_function;
   protected final Computation initial_value;
   protected final Reference collection;
   protected final boolean is_foldl;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Fold
   (
      final Origin origin,
      final Reference lambda_function,
      final Computation initial_value,
      final Reference collection,
      final boolean is_foldl,
      final Type act_as
   )
   {
      super(origin, act_as);

      this.lambda_function = lambda_function;
      this.initial_value = initial_value;
      this.collection = collection;
      this.is_foldl = is_foldl;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Fold build
   (
      final Origin origin,
      final Reference lambda_function,
      final Computation initial_value,
      final Reference collection,
      final boolean is_foldl
   )
   throws Throwable
   {
      final Type var_type, collection_generic_type;
      final CollectionType collection_type;
      final LambdaType lambda_type;
      final List<Type> signature;

      var_type = lambda_function.get_type();

      if (!(var_type instanceof LambdaType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               var_type,
               Collections.singleton(Type.LAMBDA)
            )
         );

         return null;
      }

      lambda_type = (LambdaType) var_type;

      signature = lambda_type.get_signature();

      if
      (
         (parameters.size() != signature.size())
         || (parameters.size() != 2)
      )
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               origin,
               parameters.size(),
               2,
               2
            )
         );
      }

      collection_generic_type = collection.get_type();

      if (!(collection_generic_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               collection_generic_type,
               Type.COLLECTION_TYPES
            )
         );

         return null;
      }

      collection_type = (CollectionType) collection_generic_type;

      if (!initial_value.get_type().can_be_used_as(signature.get(0)))
      {
         /* TODO */
      }

      if (!collection_type.get_member_type().can_be_used_as(signature.get(1)))
      {
         /* TODO */
      }

      return
         new Fold
         (
            origin,
            lambda_function,
            initial_value,
            collection,
            is_foldl,
            initial_value.get_type()
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_fold(this);
   }

   public Reference get_lambda_function_reference ()
   {
      return lambda_function;
   }

   public Computation get_initial_value ()
   {
      return initial_value;
   }

   public Reference get_collection ()
   {
      return collection;
   }

   public boolean is_foldl ()
   {
      return is_foldl;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (is_foldl)
      {
         sb.append("(Foldl ");
      }
      else
      {
         sb.append("(Foldr ");
      }

      sb.append(lambda_function.toString());

      sb.append(" ");
      sb.append(initial_value.toString());
      sb.append(" ");
      sb.append(collection.get_name());
      sb.append(")");

      return sb.toString();
   }
}
