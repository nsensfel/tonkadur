package tonkadur.fate.v1.lang.instruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

public class Map extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Reference collection_in;
   protected final Reference collection_out;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Map
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in,
      final Reference collection_out
   )
   {
      super(origin);

      this.lambda_function = lambda_function;
      this.collection_in = collection_in;
      this.collection_out = collection_out;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Map build
   (
      final Origin origin,
      final Computation lambda_function,
      final Reference collection_in,
      final Reference collection_out
   )
   throws Throwable
   {
      final Type var_type, collection_in_generic_type;
      final Type collection_out_generic_type;
      final CollectionType collection_in_type;
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

      if (signature.size() != 1)
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

      collection_in_generic_type = collection_in.get_type();

      if (!(collection_in_generic_type instanceof CollectionType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               collection_in.get_origin(),
               collection_in_generic_type,
               Type.COLLECTION_TYPES
            )
         );

         return null;
      }

      collection_in_type = (CollectionType) collection_in_generic_type;

      if
      (
         !collection_in_type.get_content_type().can_be_used_as(signature.get(0))
      )
      {
         /* TODO */
      }

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

      return
         new Map
         (
            origin,
            lambda_function,
            collection_in,
            collection_out
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_map(this);
   }

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Reference get_collection_in ()
   {
      return collection_in;
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

      sb.append("(Map ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection_in.toString());
      sb.append(" ");
      sb.append(collection_out.toString());
      sb.append(")");

      return sb.toString();
   }
}
