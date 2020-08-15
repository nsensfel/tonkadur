package tonkadur.fate.v1.lang.computation;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.Type;

public class LambdaExpression extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation function;
   protected final List<Variable> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   protected LambdaExpression
   (
      final Origin origin,
      final List<Variable> parameters,
      final Computation function,
      final Type type
   )
   {
      super(origin, type);

      this.function = function;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static LambdaExpression build
   (
      final Origin origin,
      final List<Variable> parameters,
      final Computation function
   )
   {
      final List<Type> signature;
      final LambdaType type;

      signature = new ArrayList<Type>();

      for (final Variable v: parameters)
      {
         signature.add(v.get_type());
      }

      type =
         new LambdaType
         (
            origin,
            function.get_type(),
            "autogen",
            signature
         );

      return new LambdaExpression(origin, parameters, function, type);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_lambda_expression(this);
   }

   public Computation get_lambda_function ()
   {
      return function;
   }

   public List<Variable> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(LambdaExpression (");

      for (final Variable param: parameters)
      {
         sb.append("(");
         sb.append(param.get_type());
         sb.append(" ");
         sb.append(param.get_name());
         sb.append(")");
      }
      sb.append(") ");

      sb.append(function.toString());

      sb.append(")");

      return sb.toString();
   }
}
