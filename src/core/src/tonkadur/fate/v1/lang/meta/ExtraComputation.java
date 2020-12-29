package tonkadur.fate.v1.lang.meta;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.computation.ExtraComputationInstance;

public class ExtraComputation extends DeclaredEntity
{
   protected static final ExtraComputation ANY;

   static
   {
      ANY =
         new ExtraComputation
         (
            Origin.BASE_LANGUAGE,
            Type.ANY,
            /*
             * Use of a space necessary to avoid conflicting with a user created
             * type.
             */
            "undetermined extra_computation",
            new ArrayList<Type>()
         );
   }

   public static ExtraComputation value_on_missing ()
   {
      return ANY;
   }

   @Override
   public /* static */ String get_type_name ()
   {
      return "ExtraComputation";
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type returned_type;
   protected final List<Type> signature;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ExtraComputation
   (
      final Origin origin,
      final Type returned_type,
      final String name,
      final List<Type> signature
   )
   {
      super(origin, name);

      this.returned_type = returned_type;
      this.signature = signature;
   }

   public ExtraComputation
   (
      final Type returned_type,
      final String name,
      final List<Type> signature
   )
   {
      super(Origin.BASE_LANGUAGE, name);

      this.returned_type = returned_type;
      this.signature = signature;
   }

   /**** Accessors ************************************************************/
   public Type get_returned_type ()
   {
      return returned_type;
   }

   public List<Type> get_signature ()
   {
      return signature;
   }

   /**** Instantiating ********************************************************/
   public ExtraComputationInstance instantiate
   (
      final World world,
      final Context context,
      final Origin origin,
      final List<Computation> parameters
   )
   throws ParsingError
   {
      return ExtraComputationInstance.build(origin, this, parameters);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append(returned_type.toString());
      sb.append(" ");
      sb.append(name);

      for (final Type t: signature)
      {
         sb.append(" ");
         sb.append(t.toString());
      }

      return name;
   }
}
