package tonkadur.fate.v1.lang.computation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.functional.Merge;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Reference;

public class LambdaEvaluation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Reference lambda_function;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected LambdaEvaluation
   (
      final Origin origin,
      final Reference lambda_function,
      final List<Computation> parameters,
      final Type act_as
   )
   {
      super(origin, act_as);

      this.lambda_function = lambda_function;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static LambdaEvaluation build
   (
      final Origin origin,
      final Reference reference,
      final List<Computation> parameters
   )
   throws Throwable
   {
      final Type var_type;
      final LambdaType lambda_type;
      final List<Type> signature;

      var_type = reference.get_type();

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

      if (parameters.size() != signature.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               origin,
               parameters.size(),
               signature.size(),
               signature.size()
            )
         );
      }

      (new Merge<Type, Computation, Boolean>()
      {
         @Override
         public Boolean risky_lambda (final Type t, final Computation p)
         throws ParsingError
         {
            if ((t == null) || (p == null))
            {
               return Boolean.FALSE;
            }
            else
            {
               final Type hint;

               if (p.get_type().can_be_used_as(t))
               {
                  return Boolean.TRUE;
               }

               if (p.get_type().try_merging_with(t) != null)
               {
                  return Boolean.TRUE;
               }

               ErrorManager.handle
               (
                  new IncompatibleTypeException
                  (
                     p.get_origin(),
                     p.get_type(),
                     t
                  )
               );

               hint = (Type) p.get_type().generate_comparable_to(t);

               if (hint.equals(Type.ANY))
               {
                  ErrorManager.handle
                  (
                     new IncomparableTypeException
                     (
                        p.get_origin(),
                        p.get_type(),
                        t
                     )
                  );
               }

               return Boolean.FALSE;
            }
         }
      }).risky_merge(signature, parameters);

      return
         new LambdaEvaluation
         (
            origin,
            reference,
            parameters,
            lambda_type.get_return_type()
         );
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_lambda_evaluation(this);
   }

   public Reference get_lambda_function_reference ()
   {
      return lambda_function;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(LambdaEvaluation (");
      sb.append(lambda_function.toString());

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append("))");

      return sb.toString();
   }
}
