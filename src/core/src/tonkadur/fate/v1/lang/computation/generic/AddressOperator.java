package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.type.PointerType;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.computation.GenericComputation;
import tonkadur.fate.v1.lang.computation.Constant;


public class AddressOperator extends GenericComputation
{
   protected static final AddressOperator ARCHETYPE;

   static
   {
      final List<String> aliases;

      ARCHETYPE =
         new AddressOperator
         (
            Origin.BASE_LANGUAGE,
            Constant.build_boolean(Origin.BASE_LANGUAGE, true)
         );

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

      try
      {
         ARCHETYPE.register(aliases, null);
      }
      catch (final Exception e)
      {
         e.printStackTrace();

         System.exit(-1);
      }
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation referred;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public AddressOperator (final Origin origin, final Computation referred)
   {
      super
      (
         origin,
         new PointerType(origin, referred.get_type(), "auto generated"),
         "address_of"
      );

      this.referred = referred;
   }

   @Override
   public GenericComputation build
   (
      final Origin origin,
      final List<Computation> call_parameters,
      final Object _registered_parameter
   )
   throws Throwable
   {
      if (call_parameters.size() != 1)
      {
         // TODO: Error.
      }

      call_parameters.get(0).expect_non_string();

      return new AddressOperator(origin, call_parameters.get(0));
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
