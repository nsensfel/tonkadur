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

import tonkadur.fate.v1.lang.TextEffect;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.RichTextNode;
import tonkadur.fate.v1.lang.meta.Computation;

public class TextWithEffect extends RichTextNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final TextEffect effect;
   protected final List<Computation> parameters;
   protected final RichTextNode text;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected TextWithEffect
   (
      final Origin origin,
      final TextEffect effect,
      final List<Computation> parameters,
      final RichTextNode text
   )
   {
      super(origin);

      this.effect = effect;
      this.parameters = parameters;
      this.text = text;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static TextWithEffect build
   (
      final Origin origin,
      final TextEffect effect,
      final List<Computation> parameters,
      final RichTextNode text
   )
   throws Throwable
   {
      final List<Type> signature;

      signature = effect.get_signature();

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

      (new Merge<Type,Computation,Boolean>()
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

      return new TextWithEffect(origin, effect, parameters, text);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_text_with_effect(this);
   }

   public TextEffect get_effect ()
   {
      return effect;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   public RichTextNode get_text ()
   {
      return text;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(TextWithEffect (");
      sb.append(effect.get_name());

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(") ");
      sb.append(text.toString());
      sb.append(")");

      return sb.toString();
   }
}
