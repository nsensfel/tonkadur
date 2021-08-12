package tonkadur.fate.v1.lang.computation.generic;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.LambdaType;
import tonkadur.fate.v1.lang.type.CollectionType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class IndexedMapComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:indexed_map");
      aliases.add("list:indexedmap");
      aliases.add("list:indexedMap");
      aliases.add("list:imap");
      aliases.add("set:indexed_map");
      aliases.add("set:indexedmap");
      aliases.add("set:indexedMap");
      aliases.add("set:imap");

      return aliases;
   }

   public static GenericComputation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      // TODO: implement
      final Computation lambda_function = null;
      final Computation collection = null;
      final List<Computation> extra_params = null;
      final List<Type> in_types;

      in_types = new ArrayList<Type>();

      RecurrentChecks.assert_is_a_collection(collection);

      in_types.add(Type.INT);
      in_types.add
      (
         ((CollectionType) collection.get_type()).get_content_type()
      );

      for (final Computation c: extra_params)
      {
         in_types.add(c.get_type());
      }

      RecurrentChecks.assert_lambda_matches_types(lambda_function, in_types);

      return
         new IndexedMapComputation
         (
            origin,
            lambda_function,
            collection,
            extra_params,
            CollectionType.build
            (
               origin,
               ((LambdaType) lambda_function.get_type()).get_return_type(),
               ((CollectionType) collection.get_type()).is_set(),
               "auto generated"
            )
         );
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Computation> extra_params;
   protected final Computation lambda_function;
   protected final Computation collection;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected IndexedMapComputation
   (
      final Origin origin,
      final Computation lambda_function,
      final Computation collection,
      final List<Computation> extra_params,
      final Type output_type
   )
   {
      super(origin, output_type);

      this.lambda_function = lambda_function;
      this.collection = collection;
      this.extra_params = extra_params;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_lambda_function ()
   {
      return lambda_function;
   }

   public Computation get_collection ()
   {
      return collection;
   }

   public List<Computation> get_extra_parameters ()
   {
      return extra_params;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(IndexedMap ");
      sb.append(lambda_function.toString());
      sb.append(" ");
      sb.append(collection.toString());

      for (final Computation c: extra_params)
      {
         sb.append(" ");
         sb.append(c.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
