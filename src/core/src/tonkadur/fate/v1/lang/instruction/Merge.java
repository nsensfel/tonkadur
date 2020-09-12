package tonkadur.fate.v1.lang.instruction;

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

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

public class Merge extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Reference collection_in_a;
   protected final Computation default_a;
   protected final Reference collection_in_b;
   protected final Computation default_b;
   protected final Reference collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Merge
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in_a,
      final Computation default_a,
      final Reference collection_in_b,
      final Computation default_b,
      final Reference collection_out
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection_in_a = collection_in_a;
      this.default_a = default_a;
      this.collection_in_b = collection_in_b;
      this.default_b = default_b;
      this.collection_out = collection_out;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Merge build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in_a,
      final Computation default_a,
      final Reference collection_in_b,
      final Computation default_b,
      final Reference collection_out
   )
   throws Throwable
   {
      final Type var_type, collection_in_a_generic_type;
      final Type collection_in_b_generic_type, collection_out_generic_type;
      final CollectionType collection_in_a_type, collection_in_b_type;
      final CollectionType collection_out_type;
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

      if (signature.size() != 2)
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               lambda_function.get_origin(),
               signature.size(),
               2,
               2
            )
         );
      }

      collection_in_a_generic_type = collection_in_a.get_type();

      if (!(collection_in_a_generic_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection_in_a.get_origin(),
               collection_in_a_generic_type,
               Type.COLLECTION_TYPES
            )
         );

         return null;
      }

      collection_in_a_type = (CollectionType) collection_in_a_generic_type;

      if
      (
         !collection_in_a_type.get_content_type().can_be_used_as
         (
            signature.get(0)
         )
      )
      {
         /* TODO */
      }

      collection_in_b_generic_type = collection_in_b.get_type();

      if (!(collection_in_b_generic_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection_in_b.get_origin(),
               collection_in_b_generic_type,
               Type.COLLECTION_TYPES
            )
         );

         return null;
      }

      collection_in_b_type = (CollectionType) collection_in_b_generic_type;

      if
      (
         !collection_in_b_type.get_content_type().can_be_used_as
         (
            signature.get(1)
         )
      )
      {
         /* TODO */
      }

      if
      (
         (default_a != null)
         &&
         (
            collection_in_a_type.get_content_type().can_be_used_as
            (
               default_a.get_type()
            )
         )
      )
      {
         /* TODO */
      }

      if
      (
         (default_b != null)
         &&
         (
            collection_in_b_type.get_content_type().can_be_used_as
            (
               default_b.get_type()
            )
         )
      )
      {
         /* TODO */
      }

      if (collection_out != null)
      {
         collection_out_generic_type = collection_out.get_type();

         if (!(collection_out_generic_type instanceof CollectionType))
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  collection_out.get_origin(),
                  collection_out_generic_type,
                  Type.COLLECTION_TYPES
               )
            );

            return null;
         }

         collection_out_type = (CollectionType) collection_out_generic_type;

         if
         (
            !collection_out_type.get_content_type().can_be_used_as
            (
               lambda_type.get_return_type()
            )
         )
         {
            /* TODO */
         }
      }

      return
         new Merge
         (
            origin,
            lambda_function,
            collection_in_a,
            default_a,
            collection_in_b,
            default_b,
            collection_out
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_merge(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Reference get_collection_in_a ()
   {
      return collection_in_a;
   }

   public Computation get_default_a ()
   {
      return default_a;
   }

   public Reference get_collection_in_b ()
   {
      return collection_in_b;
   }

   public Computation get_default_b ()
   {
      return default_b;
   }

   public Reference get_collection_out ()
   {
      return collection_out;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Merge ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection_in_a.toString());
      sb.append(" ");

      if (default_a == null)
      {
         sb.append("null");
      }
      else
      {
         sb.append(default_a.toString());
      }

      sb.append(" ");
      sb.append(collection_in_b.toString());
      sb.append(" ");

      if (default_b == null)
      {
         sb.append("null");
      }
      else
      {
         sb.append(default_b.toString());
      }

      sb.append(" ");

      if (collection_out == null)
      {
         sb.append("null");
      }
      else
      {
         sb.append(collection_out.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
