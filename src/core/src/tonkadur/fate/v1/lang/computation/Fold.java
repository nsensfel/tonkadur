package tonkadur.fate.v1.lang.computation;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class Fold extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation lambda_function;
   protected final Computation initial_value;
   protected final Computation collection;
   protected final boolean is_foldl;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Fold
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation initial_value,
      final Computation collection,
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
      final Computation lambda_function,
      final Computation initial_value,
      final Computation collection,
      final boolean is_foldl
   )
   throws ParsingError
   {
      final List<Type> types_in;

      types_in = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection);

      types_in.add(initial_value.get_type());
      types_in.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      RecurrentChecks.assert_lambda_matches_types
      (
         lambda_function,
         initial_value.get_type(),
         types_in
      );

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

   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Computation get_initial_value ()
   {
      return initial_value;
   }

   public Computation get_collection ()
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
      sb.append(collection.toString());
      sb.append(")");

      return sb.toString();
   }
}
