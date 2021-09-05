package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.WrongNumberOfParametersException;

import tonkadur.fate.v1.error.InvalidTypeException;
import tonkadur.fate.v1.error.UnknownStructureFieldException;

import tonkadur.fate.v1.lang.Variable;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.computation.Default;
import tonkadur.fate.v1.lang.computation.GenericComputation;

public class AtReference extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("at");

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
      Type current_type;

      if (call_parameters.size() != 1)
      {
         ErrorManager.handle
         (
            new WrongNumberOfParametersException
            (
               origin,
               "(" + alias + " <POINTER>)"
            )
         );

         return null;
      }

      call_parameters.get(0).expect_non_string();

      current_type = call_parameters.get(0).get_type();

      if (!(current_type instanceof PointerType))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               origin,
               current_type,
               Collections.singleton(PointerType.ARCHETYPE)
            )
         );

         current_type = Type.ANY;
      }
      else
      {
         current_type = ((PointerType) current_type).get_referenced_type();
      }

      return new AtReference(origin, current_type, call_parameters.get(0));
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected AtReference
   (
      final Origin origin,
      final Type reported_type,
      final Computation parent
   )
   {
      super(origin, reported_type);

      this.parent = parent;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_parent ()
   {
      return parent;
   }

   @Override
   public void use_as_reference ()
   throws ParsingError
   {
      // This allows its use as reference.
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(AtReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent.toString());
      sb.append(")");

      return sb.toString();
   }
}
