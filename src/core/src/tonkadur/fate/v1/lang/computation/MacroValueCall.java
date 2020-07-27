package tonkadur.fate.v1.lang.computation;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.functional.Merge;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidArityException;
import tonkadur.fate.v1.error.NotAValueMacroException;

import tonkadur.fate.v1.lang.Macro;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Computation;

public class MacroValueCall extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Macro macro;
   protected final Computation act_as;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected MacroValueCall
   (
      final Origin origin,
      final Macro macro,
      final List<Computation> parameters,
      final Computation act_as
   )
   {
      super(origin, act_as.get_type());

      this.macro = macro;
      this.parameters = parameters;
      this.act_as = act_as;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static MacroValueCall build
   (
      final Origin origin,
      final Macro macro,
      final List<Computation> parameters
   )
   throws Throwable
   {
      Computation act_as;
      final List<Type> signature;

      act_as = macro.get_value_node_representation();

      if (act_as == null)
      {
         ErrorManager.handle
         (
            new NotAValueMacroException(origin, macro.get_name())
         );
      }

      signature = macro.get_signature();

      if (parameters.size() != signature.size())
      {
         ErrorManager.handle
         (
            new InvalidArityException
            (
               origin,
               parameters.size(),
               signature.size(),
               signature.size()
            )
         );
      }

      (new Merge<Type, Computation, Boolean>()
      {
         @Override
         public Boolean risky_lambda (final Type t, final Computation p)
         throws ParsingError
         {
            if ((t == null) || (p == null))
            {
               return Boolean.FALSE;
            }
            else
            {
               final Type hint;

               if (p.get_type().can_be_used_as(t))
               {
                  return Boolean.TRUE;
               }

               if (p.get_type().try_merging_with(t) != null)
               {
                  return Boolean.TRUE;
               }

               ErrorManager.handle
               (
                  new IncompatibleTypeException
                  (
                     p.get_origin(),
                     p.get_type(),
                     t
                  )
               );

               hint = (Type) p.get_type().generate_comparable_to(t);

               if (hint.equals(Type.ANY))
               {
                  ErrorManager.handle
                  (
                     new IncomparableTypeException
                     (
                        p.get_origin(),
                        p.get_type(),
                        t
                     )
                  );
               }

               return Boolean.FALSE;
            }
         }
      }).risky_merge(signature, parameters);

      return new MacroValueCall(origin, macro, parameters, act_as);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_macro_value_call(this);
   }

   public Macro get_macro ()
   {
      return macro;
   }

   public Computation get_actual_value_node ()
   {
      return act_as;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(MacroValueCall (");
      sb.append(macro.get_name());

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append("))");

      return sb.toString();
   }
}
