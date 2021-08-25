package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.computation.GenericComputation;
import tonkadur.fate.v1.lang.computation.Constant;


public class AddressOperator extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("address_of");
      aliases.add("addressof");
      aliases.add("addressOf");

      aliases.add("address");
      aliases.add("addr");

      aliases.add("pointer_to");
      aliases.add("pointerto");
      aliases.add("pointerTo");

      aliases.add("pointer");
      aliases.add("ptr");

      aliases.add("reference_to");
      aliases.add("referenceto");
      aliases.add("referenceTo");

      aliases.add("reference");
      aliases.add("ref");

      return aliases;
   }

   public static Computation build
   (
      final Origin origin,
      final String _alias,
      final List<Computation> call_parameters
   )
   throws Throwable
   {
      if (call_parameters.size() != 1)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      call_parameters.get(0).expect_non_string();

      return new AddressOperator(origin, call_parameters.get(0));
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation referred;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddressOperator (final Origin origin, final Computation referred)
   {
      super
      (
         origin,
         new PointerType(origin, referred.get_type(), "auto generated")
      );

      this.referred = referred;
   }


   /**** Accessors ************************************************************/
   public Computation get_target ()
   {
      return referred;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AddressOf ");
      sb.append(referred.toString());
      sb.append(") ");

      return sb.toString();
   }
}
