package tonkadur.fate.v1.lang.computation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class Cast extends Computation
{
   protected static final Map<Type, Set<Type>> allowed_type_changes;

   static
   {
      Set allowed_targets;

      allowed_type_changes = new HashMap<Type,Set<Type>>();

      /** INT to ... **********************************************************/
      allowed_targets = new HashSet<Type>();
      allowed_targets.add(Type.FLOAT);
      allowed_targets.add(Type.INT);
      allowed_targets.add(Type.STRING);

      allowed_type_changes.put(Type.INT, allowed_targets);

      /** FLOAT to ... ********************************************************/
      allowed_targets = new HashSet<Type>();
      allowed_targets.add(Type.FLOAT);
      allowed_targets.add(Type.INT);
      allowed_targets.add(Type.STRING);

      allowed_type_changes.put(Type.FLOAT, allowed_targets);

      /** BOOL to ... *********************************************************/
      allowed_targets = new HashSet<Type>();
      allowed_targets.add(Type.BOOL);
      allowed_targets.add(Type.STRING);

      allowed_type_changes.put(Type.BOOL, allowed_targets);

      /** BOOL to ... *********************************************************/
      allowed_targets = new HashSet<Type>();
      allowed_targets.add(Type.BOOL);
      allowed_targets.add(Type.INT);
      allowed_targets.add(Type.FLOAT);
      allowed_targets.add(Type.STRING);

      allowed_type_changes.put(Type.STRING, allowed_targets);
   }

   public static Collection<Type> get_allowed_casts_to (final Type t)
   {
      return allowed_type_changes.get(t);
   }

   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Computation value;
   protected final boolean is_autogenerated;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Cast
   (
      final Origin origin,
      final Type to,
      final Computation value,
      final boolean is_autogenerated
   )
   {
      super(origin, to);
      this.value = value;
      this.is_autogenerated = is_autogenerated;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static Cast build
   (
      final Origin origin,
      final Type to,
      final Computation value,
      final boolean is_autogenerated
   )
   throws
      IncompatibleTypeException,
      IncomparableTypeException
   {
      final Type hint;

      value.expect_string();

      if
      (
         (value.get_type().can_be_used_as(to))
         ||
         (
            allowed_type_changes.containsKey
            (
               value.get_type().get_act_as_type().get_base_type()
            )
            &&
            allowed_type_changes.get
            (
               value.get_type().get_act_as_type().get_base_type()
            ).contains(to.get_act_as_type())
         )
      )
      {
         return new Cast(origin, to, value, is_autogenerated);
      }

      hint = (Type) value.get_type().generate_comparable_to(to);

      if (hint.equals(Type.ANY))
      {
         ErrorManager.handle
         (
            new IncompatibleTypeException(origin, value.get_type(), to)
         );

         ErrorManager.handle
         (
            new IncomparableTypeException(origin, value.get_type(), to)
         );
      }
      else
      {
         ErrorManager.handle
         (
            new IncompatibleTypeException(origin, value.get_type(), to, hint)
         );
      }

      return new Cast(origin, hint, value, is_autogenerated);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_cast(this);
   }

   public Computation get_parent ()
   {
      return value;
   }

   public boolean is_autogenerated ()
   {
      return is_autogenerated;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(Cast (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(value.toString());
      sb.append(")");

      return sb.toString();
   }
}
