package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;

import tonkadur.fate.v1.lang.computation.GenericComputation;

import tonkadur.fate.v1.lang.computation.LambdaExpression;

public class UserComputation extends GenericComputation
{
   protected static final Map<String, LambdaExpression> DEFINITIONS;

   static
   {
      DEFINITIONS = new HashMap<String, LambdaExpression>();
   }

   public static Collection<String> get_aliases ()
   {
      // Will be added individually later.
      return new ArrayList<String>();
   }

   public static void register
   (
      final String name,
      final LambdaExpression sequence
   )
   {
      GenericComputation.register(name, UserComputation.class);

      DEFINITIONS.put(name, sequence);
   }

   public static Set<Map.Entry<String, LambdaExpression>> get_all_definitions ()
   {
      return DEFINITIONS.entrySet();
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final LambdaType lambda_type;
      final LambdaExpression definition;

      definition = DEFINITIONS.get(alias);

      if (definition == null)
      {
         System.err.println(origin.toString());
         System.err.println
         (
            "[F] Programming error: the User Computation '"
            + alias
            + "' was registered without an associated LambdaExpression."
         );
         System.exit(-1);
      }

      lambda_type = ((LambdaType) definition.get_type());

      RecurrentChecks.propagate_expected_types_and_assert_computations_matches_signature
      (
         origin,
         call_parameters,
         lambda_type.get_signature()
      );

      return
         new UserComputation
         (
            origin,
            lambda_type.get_return_type(),
            alias,
            call_parameters
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String computation_name;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected UserComputation
   (
      final Origin origin,
      final Type return_type,
      final String computation_name,
      final List<Computation> parameters
   )
   {
      super(origin, return_type);

      this.computation_name = computation_name;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public String get_computation_name ()
   {
      return computation_name;
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

      sb.append("(");
      sb.append(get_computation_name());

      for (final Computation param: get_parameters())
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
