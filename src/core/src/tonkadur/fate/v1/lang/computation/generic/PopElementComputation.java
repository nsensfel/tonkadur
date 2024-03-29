package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.computation.GenericComputation;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.ConsType;
import tonkadur.fate.v1.lang.type.CollectionType;

public class PopElementComputation extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:popleft");
      aliases.add("list:pop_left");
      aliases.add("list:popLeft");
      aliases.add("list:popleftelement");
      aliases.add("list:pop_left_element");
      aliases.add("list:popLeftElement");
      aliases.add("list:popright");
      aliases.add("list:pop_right");
      aliases.add("list:popRight");
      aliases.add("list:poprightelement");
      aliases.add("list:pop_right_element");
      aliases.add("list:popRightElement");
      aliases.add("set:popleft");
      aliases.add("set:pop_left");
      aliases.add("set:popLeft");
      aliases.add("set:popleftelement");
      aliases.add("set:pop_left_element");
      aliases.add("set:popLeftElement");
      aliases.add("set:popright");
      aliases.add("set:pop_right");
      aliases.add("set:popRight");
      aliases.add("set:poprightelement");
      aliases.add("set:pop_right_element");
      aliases.add("set:popRightElement");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      final Computation collection;
      final boolean is_from_left;

      if (call_parameters.size() != 1)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + " <(LIST X)|(SET X)>)"
            )
         );

         return null;
      }

      collection = call_parameters.get(0);

      collection.expect_non_string();

      is_from_left = alias.contains("eft");

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(collection);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(collection);
      }

      return
         new PopElementComputation
         (
            origin,
            collection,
            is_from_left,
            new ConsType
            (
               origin,
               ((CollectionType) collection.get_type()).get_content_type(),
               collection.get_type(),
               "auto gen"
            )
         );
   }
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation collection;
   protected final boolean is_from_left;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PopElementComputation
   (
      final Origin origin,
      final Computation collection,
      final boolean is_from_left,
      final Type type
   )
   {
      super(origin, type);

      this.collection = collection;
      this.is_from_left = is_from_left;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_collection ()
   {
      return collection;
   }

   public boolean is_from_left ()
   {
      return is_from_left;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      if (is_from_left)
      {
         sb.append("(PopLeftElement ");
      }
      else
      {
         sb.append("(PopRightElement ");
      }

      sb.append(collection.toString());

      sb.append(")");

      return sb.toString();
   }
}
