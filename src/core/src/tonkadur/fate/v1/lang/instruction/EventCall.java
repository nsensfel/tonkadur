package tonkadur.fate.v1.lang.instruction;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.functional.Merge;

import tonkadur.error.ErrorManager;

import tonkadur.fate.v1.error.IncompatibleTypeException;
import tonkadur.fate.v1.error.IncomparableTypeException;
import tonkadur.fate.v1.error.InvalidArityException;

import tonkadur.fate.v1.lang.Event;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.NodeVisitor;
import tonkadur.fate.v1.lang.meta.InstructionNode;
import tonkadur.fate.v1.lang.meta.ValueNode;

public class EventCall extends InstructionNode
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Event event;
   protected final List<ValueNode> parameters;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected EventCall
   (
      final Origin origin,
      final Event event,
      final List<ValueNode> parameters
   )
   {
      super(origin);

      this.event = event;
      this.parameters = parameters;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public static EventCall build
   (
      final Origin origin,
      final Event event,
      final List<ValueNode> parameters
   )
   throws Throwable
   {
      final List<Boolean> type_checks;
      final List<Type> signature;

      signature = event.get_signature();

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

      (new Merge<Type, ValueNode, Boolean>()
      {
         @Override
         public Boolean risky_lambda (final Type t, final ValueNode p)
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

      return new EventCall(origin, event, parameters);
   }

   /**** Accessors ************************************************************/
   @Override
   public void visit (final NodeVisitor nv)
   throws Throwable
   {
      nv.visit_event_call(this);
   }

   public Event get_event ()
   {
      return event;
   }

   public List<ValueNode> get_parameters ()
   {
      return parameters;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(EventCall (");
      sb.append(event.get_name());

      for (final ValueNode param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append("))");

      return sb.toString();
   }
}
