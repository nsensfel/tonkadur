package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonkadur.functional.Cons;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

import tonkadur.fate.v1.lang.type.Type;

public class ExtraComputation extends GenericComputation
{
   protected static final Map<String, Cons<Type, List<Type>>> SIGNATURES;

   static
   {
      SIGNATURES = new HashMap<String, Cons<Type, List<Type>>>();
   }

   public static Collection<String> get_aliases ()
   {
      // These are added individually later.
      return new ArrayList<String>();
   }

   public static void register
   (
      final Type return_type,
      final String name,
      final List<Type> signature
   )
   {
      GenericComputation.register(name, ExtraComputation.class);

      SIGNATURES.put(name, new Cons(return_type, signature));
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
   protected ExtraComputation
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
   /**** Constructors *********************************************************/
   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Cons<Type, List<Type>> signature;

      signature = SIGNATURES.get(alias);

      if (signature == null)
      {
         System.err.println(origin.toString());
         System.err.println
         (
            "[F] Programming error: the Extra Computation'"
            + alias
            + "' was registered without a signature."
         );
         System.exit(-1);
      }

      RecurrentChecks.propagate_expected_types_and_assert_computations_matches_signature
      (
         origin,
         call_parameters,
         signature.get_cdr()
      );

      return
         new ExtraComputation
         (
            origin,
            signature.get_car(),
            alias,
            call_parameters
         );
   }

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
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(");
      sb.append(get_computation_name());

      for (final Computation p: parameters)
      {
         sb.append(" ");
         sb.append(p.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
