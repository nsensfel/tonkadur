package tonkadur.fate.v1.lang.computation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import tonkadur.parser.Origin;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.InvalidTypeException;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

import tonkadur.fate.v1.lang.type.CollectionType;
import tonkadur.fate.v1.lang.type.PointerType;
import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.computation.GenericComputation;

public class Access extends GenericComputation
{
   public static Collection<String> get_aliases ()
   {
      final Collection<String> aliases;

      aliases = new ArrayList<String>();

      aliases.add("list:access");
      aliases.add("list:at_index");
      aliases.add("list:atindex");
      aliases.add("list:atIndex");
      aliases.add("set:access");
      aliases.add("set:at_index");
      aliases.add("set:atindex");
      aliases.add("set:atIndex");

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
      final Computation index;
      Computation parent;
      Type current_type;

      if (call_parameters.size() != 2)
      {
         // TODO: Error.
         System.err.println("Wrong number of params at " + origin.toString());

         return null;
      }

      index = call_parameters.get(0);
      parent = call_parameters.get(1);

      index.expect_non_string();
      parent.expect_non_string();

      current_type = parent.get_type();

      while (current_type.can_be_used_as(PointerType.ARCHETYPE))
      {
         parent =
            AtReference.build
            (
               origin,
               "at",
               Collections.singletonList(parent)
            );

         current_type = parent.get_type();
      }

      if (!index.get_type().can_be_used_as(Type.INT))
      {
         ErrorManager.handle
         (
            new InvalidTypeException
            (
               index.get_origin(),
               current_type,
               Collections.singleton(Type.INT),
               index.toString()
            )
         );
      }

      if (alias.startsWith("set:"))
      {
         RecurrentChecks.assert_is_a_set(parent);
      }
      else
      {
         RecurrentChecks.assert_is_a_list(parent);
      }

      return new Access(origin, parent, current_type, index);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation parent;
   protected final Computation index;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Access
   (
      final Origin origin,
      final Computation parent,
      final Type type,
      final Computation index
   )
   {
      super(origin, type);

      this.parent = parent;
      this.index = index;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public Computation get_index ()
   {
      return index;
   }

   public Computation get_parent ()
   {
      return parent;
   }

   @Override
   public void use_as_reference ()
   throws ParsingError
   {
      parent.use_as_reference();
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Access (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(parent.toString());
      sb.append(".");
      sb.append(index.toString());
      sb.append(")");

      return sb.toString();
   }
}
