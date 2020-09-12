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

public class Sort extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Reference collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Sort
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection = collection;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Sort build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection
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

      if (signature.size() != 2)
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               lambda_function.get_origin(),
               signature.size(),
               1,
               1
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
               collection.get_origin(),
               collection_generic_type,
               Type.COLLECTION_TYPES
            )
         );

         return null;
      }

      collection_type = (CollectionType) collection_generic_type;

      if (!collection_type.get_content_type().can_be_used_as(signature.get(0)))
      {
         /* TODO */
      }

      if (!collection_type.get_content_type().can_be_used_as(signature.get(1)))
      {
         /* TODO */
      }

      if (!lambda_type.get_return_type().can_be_used_as(Type.INT))
      {
         /* TODO */
      }

      return new Sort(origin, lambda_function, collection);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_sort(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Reference get_collection ()
   {
      return collection;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Sort ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
