package tonkadur.fate.v1.lang.instruction;

import java.util.ArrayList;
import java.util.List;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.lang.Event;

import tonkadur.fate.v1.lang.type.Type;

import tonkadur.fate.v1.lang.meta.InstructionVisitor;
import tonkadur.fate.v1.lang.meta.Instruction;
import tonkadur.fate.v1.lang.meta.Computation;
import tonkadur.fate.v1.lang.meta.RecurrentChecks;

public class PlayerInput extends Instruction
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Event input_event;
   protected final List<Computation> params;
   protected final List<Instruction> effects;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected PlayerInput
   (
      final Origin origin,
      final Event input_event,
      final List<Computation> params,
      final List<Instruction> effects
   )
   {
      super(origin);

      this.input_event = input_event;
      this.params = params;
      this.effects = effects;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public PlayerInput
   (
      final Origin origin,
      final Event input_event,
      final List<Instruction> effects
   )
   {
      super(origin);

      this.input_event = input_event;
      this.params = new ArrayList<Computation>();
      this.effects = effects;
   }

   public static PlayerInput build
   (
      final Origin origin,
      final Event event,
      final List<Computation> parameters,
      final List<Instruction> effects
   )
   throws ParsingError
   {
      RecurrentChecks.assert_computations_matches_signature
      (
         origin,
         parameters,
         event.get_signature()
      );

      return new PlayerInput(origin, event, parameters, effects);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final InstructionVisitor iv)
   throws Throwable
   {
      iv.visit_player_input(this);
   }

   public Event get_input_event ()
   {
      return input_event;
   }

   public List<Computation> get_parameters ()
   {
      return params;
   }

   public List<Instruction> get_effects ()
   {
      return effects;
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(PlayerInput (");
      sb.append(System.lineSeparator());
      sb.append(input_event.toString());

      for (final Computation param: params)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(")");

      for (final Instruction effect: effects)
      {
         sb.append(System.lineSeparator());
         sb.append(effect.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
