package tonkadur.fate.v1.lang.meta;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Context;
import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.World;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.instruction.ExtraInstructionInstance;

public class ExtraInstruction extends DeclaredEntity
{
   protected static final ExtraInstruction ANY;

   static
   {
      ANY =
         new ExtraInstruction
         (
            Origin.BASE_LANGUAGE,
            /*
             * Use of a space necessary to avoid conflicting with a user created
             * type.
             */
            "undetermined extra_instruction",
            new ArrayList<Type>()
         );
   }

   public static ExtraInstruction value_on_missing ()
   {
      return ANY;
   }

   @Override
   public /* static */ String get_type_name ()
   {
      return "ExtraInstruction";
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final List<Type> signature;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ExtraInstruction
   (
      final Origin origin,
      final String name,
      final List<Type> signature
   )
   {
      super(origin, name);

      this.signature = signature;
   }

   public ExtraInstruction
   (
      final String name,
      final List<Type> signature
   )
   {
      super(Origin.BASE_LANGUAGE, name);

      this.signature = signature;
   }

   /**** Accessors ************************************************************/
   public List<Type> get_signature ()
   {
      return signature;
   }

   /**** Instantiating ********************************************************/
   public ExtraInstructionInstance instantiate
   (
      final World world,
      final Context context,
      final Origin origin,
      final List<Computation> parameters
   )
   throws ParsingError
   {
      return ExtraInstructionInstance.build(origin, this, parameters);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append(name);

      for (final Type t: signature)
      {
         sb.append(" ");
         sb.append(t.toString());
      }

      return name;
   }
}
