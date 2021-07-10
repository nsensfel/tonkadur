parser grammar FateParser;

options
{
   tokenVocab = FateLexer;
}

@header
{
   package tonkadur.fate.v1.parser;

   import java.util.ArrayDeque;
   import java.util.Arrays;
   import java.util.Deque;
   import java.util.Collection;
   import java.util.Map;
   import java.util.HashMap;
   import java.util.HashSet;

   import tonkadur.Files;

   import tonkadur.error.ErrorManager;

   import tonkadur.functional.Cons;

   import tonkadur.parser.Context;
   import tonkadur.parser.Location;
   import tonkadur.parser.Origin;

/* Conflicts with an existing package */
/*   import tonkadur.fate.v1.tonkadur.fate.v1.Utils; */

   import tonkadur.fate.v1.error.DuplicateLocalVariableException;
   import tonkadur.fate.v1.error.IllegalReferenceNameException;
   import tonkadur.fate.v1.error.InvalidArityException;
   import tonkadur.fate.v1.error.InvalidTypeException;
   import tonkadur.fate.v1.error.UpdatingIllegalVariableFromChoiceException;
   import tonkadur.fate.v1.error.UnknownExtensionContentException;

   import tonkadur.fate.v1.lang.*;
   import tonkadur.fate.v1.lang.instruction.*;
   import tonkadur.fate.v1.lang.meta.*;
   import tonkadur.fate.v1.lang.type.*;
   import tonkadur.fate.v1.lang.computation.*;
}

@members
{
   ParserData PARSER;
}

/******************************************************************************/
/******************************************************************************/
/******************************************************************************/
fate_file [ParserData parser]
@init
{
   PARSER = parser;

   PARSER.increase_local_variables_hierarchy();
}
:
   WS* FATE_VERSION_KW WORD WS* R_PAREN WS*
   (
      (
         first_level_instruction
         |
         (
            instruction
            {
               PARSER.get_world().add_global_instruction(($instruction.result));
            }
         )
      )
      WS*
   )*
   EOF
   {
   }
;

maybe_instruction_list
returns [List<Instruction> result]
@init
{
   $result = new ArrayList<Instruction>();
}
:
   (WS*
      instruction
      {
         $result.add(($instruction.result));
      }
   WS*)*
   {
   }
;

first_level_instruction
@init
{
   ParserData.LocalVariables previous_local_variables_stack;
}
:
   DECLARE_ALIAS_TYPE_KW parent=type WS+ identifier WS* R_PAREN
   {
      final Origin start_origin;
      final Type new_type;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_ALIAS_TYPE_KW.getLine()),
            ($DECLARE_ALIAS_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         ($parent.result).generate_alias
         (
            start_origin,
            ($identifier.result)
         );

      PARSER.get_world().types().add(new_type);
   }

   | DECLARE_STRUCT_TYPE_KW identifier WS* variable_list WS* R_PAREN
   {
      final Origin start_origin;
      final Type new_type;
      final Map<String, Type> field_types;

      field_types = new HashMap<String, Type>();

      for (final Variable te: ($variable_list.result).get_entries())
      {
         field_types.put(te.get_name(), te.get_type());
      }

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_STRUCT_TYPE_KW.getLine()),
            ($DECLARE_STRUCT_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         new StructType
         (
            start_origin,
            field_types,
            ($identifier.result)
         );

      PARSER.get_world().types().add(new_type);
   }

   | DECLARE_EXTRA_INSTRUCTION_KW identifier maybe_type_list WS* R_PAREN
   {
      final Origin start_origin;
      final ExtraInstruction extra_instruction;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_EXTRA_INSTRUCTION_KW.getLine()),
            ($DECLARE_EXTRA_INSTRUCTION_KW.getCharPositionInLine())
         );

      extra_instruction =
         new ExtraInstruction
         (
            start_origin,
            ($identifier.result),
            ($maybe_type_list.result)
         );

      PARSER.get_world().extra_instructions().add(extra_instruction);
   }

   | DECLARE_EXTRA_COMPUTATION_KW
         type WS+
         identifier
         maybe_type_list WS*
      R_PAREN
   {
      final Origin start_origin;
      final ExtraComputation extra_computation;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_EXTRA_COMPUTATION_KW.getLine()),
            ($DECLARE_EXTRA_COMPUTATION_KW.getCharPositionInLine())
         );

      extra_computation =
         new ExtraComputation
         (
            start_origin,
            ($type.result),
            ($identifier.result),
            ($maybe_type_list.result)
         );

      PARSER.get_world().extra_computations().add(extra_computation);
   }

   | DECLARE_EXTRA_TYPE_KW identifier WS+ argc=WORD WS+ comp=WORD WS* R_PAREN
   {
      final Type new_type;
      final Origin start_origin;
      int arg_count;
      boolean is_comparable;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_EXTRA_TYPE_KW.getLine()),
            ($DECLARE_EXTRA_TYPE_KW.getCharPositionInLine())
         );

      try
      {
         arg_count = Integer.parseInt($argc.text);

         if (arg_count < 0)
         {
            // TODO: show error.
         }
      }
      catch (final Exception e)
      {
         arg_count = 0;
         // TODO: show error.
         throw e;
      }

      try
      {
         is_comparable = Boolean.parseBoolean($comp.text);
      }
      catch (final Exception e)
      {
         is_comparable = false;
         // TODO: show error.
         throw e;
      }

      new_type =
         new ExtraType
         (
            start_origin,
            null,
            ($identifier.result),
            Type.generate_default_parameters(arg_count)
         );

      PARSER.get_world().types().add(new_type);

      if (is_comparable)
      {
         Type.COMPARABLE_TYPES.add(new_type);
      }
   }

   | DECLARE_EVENT_TYPE_KW identifier maybe_type_list WS* R_PAREN
   {
      final Origin start_origin;
      final Event new_event;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_EVENT_TYPE_KW.getLine()),
            ($DECLARE_EVENT_TYPE_KW.getCharPositionInLine())
         );

      new_event =
         new Event
         (
            start_origin,
            ($maybe_type_list.result),
            ($identifier.result)
         );

      PARSER.get_world().events().add(new_event);
   }

   | DECLARE_TEXT_EFFECT_KW identifier maybe_type_list WS* R_PAREN
   {
      final Origin start_origin;
      final TextEffect new_text_effect;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_TEXT_EFFECT_KW.getLine()),
            ($DECLARE_TEXT_EFFECT_KW.getCharPositionInLine())
         );

      new_text_effect =
         new TextEffect
         (
            start_origin,
            ($maybe_type_list.result),
            ($identifier.result)
         );

      PARSER.get_world().text_effects().add(new_text_effect);
   }

   | DECLARE_GLOBAL_VARIABLE_KW type WS+ name=identifier WS* R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_GLOBAL_VARIABLE_KW.getLine()),
            ($DECLARE_GLOBAL_VARIABLE_KW.getCharPositionInLine())
         );

      new_variable =
         new Variable
         (
            start_origin,
            ($type.result),
            ($name.result),
            false
         );

      PARSER.get_world().variables().add(new_variable);
   }

   | DECLARE_EXTERNAL_VARIABLE_KW type WS+ name=identifier WS* R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_EXTERNAL_VARIABLE_KW.getLine()),
            ($DECLARE_EXTERNAL_VARIABLE_KW.getCharPositionInLine())
         );

      new_variable =
         new Variable
         (
            start_origin,
            ($type.result),
            ($name.result),
            true
         );

      PARSER.get_world().variables().add(new_variable);
   }

   | DEFINE_SEQUENCE_KW
         {
            previous_local_variables_stack = PARSER.get_local_variables_stack();
            PARSER.discard_local_variables_stack();
            PARSER.increase_local_variables_hierarchy();
         }
         identifier
         WS*
         (
            L_PAREN WS* variable_list WS* R_PAREN
            {
               PARSER.add_local_variables(($variable_list.result).as_map());
               PARSER.increase_local_variables_hierarchy();
            }
         )
         pre_sequence_point=WS+
         maybe_instruction_list
         WS*
      R_PAREN
   {
      final Origin start_origin, sequence_origin;
      final Sequence new_sequence;

      start_origin =
         PARSER.get_origin_at
         (
            ($DEFINE_SEQUENCE_KW.getLine()),
            ($DEFINE_SEQUENCE_KW.getCharPositionInLine())
         );

      sequence_origin =
         PARSER.get_origin_at
         (
            ($pre_sequence_point.getLine()),
            ($pre_sequence_point.getCharPositionInLine())
         );

      new_sequence =
         new Sequence
         (
            start_origin,
            new InstructionList
            (
               sequence_origin,
               ($maybe_instruction_list.result)
            ),
            ($identifier.result),
            ($variable_list.result).get_entries()
         );

      PARSER.get_world().sequences().add(new_sequence);
      PARSER.restore_local_variables_stack(previous_local_variables_stack);
   }


   | IMP_IGNORE_ERROR_KW WORD WS+ first_level_instruction WS* R_PAREN
   {
      /* TODO: temporarily disable an compiler error category */
   }


   | REQUIRE_EXTENSION_KW WORD WS* R_PAREN
   {
      PARSER.get_world().add_required_extension(($WORD.text));

      /* TODO: error report if extension not explicitly enabled. */
   }

   | REQUIRE_KW WORD WS* R_PAREN
   {
      final String filename;

      filename = Files.resolve_filename(PARSER.get_context(), ($WORD.text));

      if (!PARSER.get_world().has_loaded_file(filename))
      {
         PARSER.add_file_content
         (
            PARSER.get_origin_at
            (
               ($REQUIRE_KW.getLine()),
               ($REQUIRE_KW.getCharPositionInLine())
            ),
            filename
         );
      }
   }

   | INCLUDE_KW WORD WS* R_PAREN
   {
      final String filename;

      filename = Files.resolve_filename(PARSER.get_context(), ($WORD.text));

      PARSER.add_file_content
      (
         PARSER.get_origin_at
         (
            ($INCLUDE_KW.getLine()),
            ($INCLUDE_KW.getCharPositionInLine())
         ),
         filename
      );
   }

   /*
   | EXTENSION_FIRST_LEVEL_KW WORD WS+ instruction_list WS* R_PAREN
   {
      final Origin origin;
      final ExtensionInstruction instr;

      origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      instr =
         PARSER.get_world().extension_first_level_instructions().get
         (
            ($WORD.text)
         );

      if (instr == null)
      {
         ErrorManager.handle
         (
            new UnknownExtensionContentException(origin, ($WORD.text))
         );
      }
      else
      {
         instr.build(WORLD, CONTEXT, origin, ($instruction_list.result));
      }
   }
   */
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

/* Trying to get rule priorities right */
instruction
returns [Instruction result]
:
   L_PAREN WS* maybe_instruction_list WS* R_PAREN
   {
      /*
       * Don't define a local variable hierachy just for that group, as it would
       * prevent things like
       * (for
       *    (
       *       (local int i)
       *       (set i 0)
       *    )
       *    (< i 3)
       *    (set i (+ i 1))
       *    stuff
       * )
       *
       */
      $result =
         new InstructionList
         (
            PARSER.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($maybe_instruction_list.result)
         );
   }

   | DECLARE_LOCAL_VARIABLE_KW type WS+ name=identifier WS* R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;
      final Map<String, Variable> variable_map;

      start_origin =
         PARSER.get_origin_at
         (
            ($DECLARE_LOCAL_VARIABLE_KW.getLine()),
            ($DECLARE_LOCAL_VARIABLE_KW.getCharPositionInLine())
         );

      new_variable =
         new Variable
         (
            start_origin,
            ($type.result),
            ($name.result),
            false
         );

      PARSER.add_local_variable(new_variable);

      $result = new LocalVariable(new_variable);
   }

/******************************************************************************/
/**** LOOPS *******************************************************************/
/******************************************************************************/
   | COND_KW instruction_cond_list WS* R_PAREN
   {
      $result =
         CondInstruction.build
         (
            PARSER.get_origin_at
            (
               ($COND_KW.getLine()),
               ($COND_KW.getCharPositionInLine())
            ),
            ($instruction_cond_list.result)
         );
   }

   | DO_WHILE_KW computation WS*
         {
            PARSER.increment_breakable_levels();
            PARSER.increment_continue_levels();
            PARSER.increase_local_variables_hierarchy();
         }
         maybe_instruction_list WS*
      R_PAREN
   {
      PARSER.decrease_local_variables_hierarchy();
      PARSER.decrement_continue_levels();
      PARSER.decrement_breakable_levels();

      $result =
         DoWhile.build
         (
            PARSER.get_origin_at
            (
               ($DO_WHILE_KW.getLine()),
               ($DO_WHILE_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($maybe_instruction_list.result)
         );
   }

   | FOR_KW
         {
            PARSER.increase_local_variables_hierarchy();
         }
         pre=instruction WS* computation WS* post=instruction WS*
         {
            PARSER.increment_breakable_levels();
            PARSER.increment_continue_levels();
         }
         maybe_instruction_list
         WS*
      R_PAREN
   {
      PARSER.decrement_continue_levels();
      PARSER.decrement_breakable_levels();
      PARSER.decrease_local_variables_hierarchy();

      $result =
         For.build
         (
            PARSER.get_origin_at
            (
               ($FOR_KW.getLine()),
               ($FOR_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($pre.result),
            ($maybe_instruction_list.result),
            ($post.result)
         );
   }

   | FOR_EACH_KW coll=computation WS+ identifier
      {
         final Variable new_variable;
         final Type collection_type;
         Type elem_type;

         elem_type = Type.ANY;

         collection_type = ($coll.result).get_type();

         /* FIXME: This doesn't let you use it with a Dict. */
         if (collection_type instanceof CollectionType)
         {
            elem_type = ((CollectionType) collection_type).get_content_type();
         }
         else
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  PARSER.get_origin_at
                  (
                     ($FOR_EACH_KW.getLine()),
                     ($FOR_EACH_KW.getCharPositionInLine())
                  ),
                  elem_type,
                  Type.COLLECTION_TYPES
               )
            );

            elem_type = Type.ANY;
         }

         new_variable =
            new Variable
            (
               PARSER.get_origin_at
               (
                  ($FOR_EACH_KW.getLine()),
                  ($FOR_EACH_KW.getCharPositionInLine())
               ),
               elem_type,
               ($identifier.result),
               false
            );

         PARSER.increase_local_variables_hierarchy();
         PARSER.increment_breakable_levels();
         PARSER.increment_continue_levels();

         PARSER.add_local_variable(new_variable);
      }
      WS+ maybe_instruction_list WS* R_PAREN
   {
      PARSER.decrement_continue_levels();
      PARSER.decrement_breakable_levels();
      PARSER.decrease_local_variables_hierarchy();

      $result =
         new ForEach
         (
            PARSER.get_origin_at
            (
               ($FOR_EACH_KW.getLine()),
               ($FOR_EACH_KW.getCharPositionInLine())
            ),
            ($coll.result),
            ($identifier.result),
            ($maybe_instruction_list.result)
         );
   }

   | WHILE_KW computation WS*
      {
         PARSER.increase_local_variables_hierarchy();
         PARSER.increment_breakable_levels();
         PARSER.increment_continue_levels();
      }
      maybe_instruction_list WS* R_PAREN
   {
      PARSER.decrement_continue_levels();
      PARSER.decrement_breakable_levels();
      PARSER.decrease_local_variables_hierarchy();

      $result =
         While.build
         (
            PARSER.get_origin_at
            (
               ($WHILE_KW.getLine()),
               ($WHILE_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($maybe_instruction_list.result)
         );
   }

   | SWITCH_KW computation WS*
         {
            PARSER.increment_breakable_levels();
         }
         instruction_cond_list WS*
         {
            PARSER.increase_local_variables_hierarchy();
         }
         instruction WS*
      R_PAREN
   {
      PARSER.decrease_local_variables_hierarchy();
      PARSER.decrement_breakable_levels();

      $result =
         SwitchInstruction.build
         (
            PARSER.get_origin_at
            (
               ($SWITCH_KW.getLine()),
               ($SWITCH_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($instruction_cond_list.result),
            ($instruction.result)
         );
   }

/******************************************************************************/
/**** IF ELSE *****************************************************************/
/******************************************************************************/
   | IF_KW computation WS*
         {
            PARSER.increase_local_variables_hierarchy();
         }
         maybe_instruction_list WS*
      R_PAREN
   {
      PARSER.decrease_local_variables_hierarchy();

      $result =
         IfInstruction.build
         (
            PARSER.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($maybe_instruction_list.result)
         );
   }

   | IF_ELSE_KW computation
         {
            PARSER.increase_local_variables_hierarchy();
         }
         WS* if_true=instruction
         {
            PARSER.decrease_local_variables_hierarchy();
            PARSER.increase_local_variables_hierarchy();
         }
         WS* if_false=instruction WS*
      R_PAREN
   {
      PARSER.decrease_local_variables_hierarchy();

      $result =
         IfElseInstruction.build
         (
            PARSER.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

/******************************************************************************/
/**** ERRORS ******************************************************************/
/******************************************************************************/

   | IMP_IGNORE_ERROR_KW WORD WS+ instruction WS* R_PAREN
   {
      /* TODO: temporarily disable an compiler error category */
      $result = ($instruction.result);
   }

   | IMP_ASSERT_KW computation WS+ paragraph WS* R_PAREN
   {
      $result =
         Assert.build
         (
            PARSER.get_origin_at
            (
               ($IMP_ASSERT_KW.getLine()),
               ($IMP_ASSERT_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($paragraph.result)
         );
   }

/******************************************************************************/
/**** STRUCTURES **************************************************************/
/******************************************************************************/

   | IMP_SET_FIELDS_KW computation WS* field_value_list WS* R_PAREN
   {
      $result =
         new SetFields
         (
            PARSER.get_origin_at
            (
               ($IMP_SET_FIELDS_KW.getLine()),
               ($IMP_SET_FIELDS_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($field_value_list.result)
         );
   }

/******************************************************************************/
/**** PLAYER INPUTS ***********************************************************/
/******************************************************************************/

   | PLAYER_CHOICE_KW
         player_choice_list[PARSER.generate_player_choice_data()]
         WS*
      R_PAREN
   {
      $result =
         new PlayerChoice
         (
            PARSER.get_origin_at
            (
               ($PLAYER_CHOICE_KW.getLine()),
               ($PLAYER_CHOICE_KW.getCharPositionInLine())
            ),
            ($player_choice_list.result)
         );
   }

   | PROMPT_STRING_KW
         targetv=computation WS+
         min_size=computation WS+
         max_size=computation WS+
         paragraph WS*
      R_PAREN
   {
      $result =
         PromptString.build
         (
            PARSER.get_origin_at
            (
               ($PROMPT_STRING_KW.getLine()),
               ($PROMPT_STRING_KW.getCharPositionInLine())
            ),
            ($targetv.result),
            ($min_size.result),
            ($max_size.result),
            ($paragraph.result)
         );
   }

   | PROMPT_INTEGER_KW
         targetv=computation WS+
         min_size=computation WS+
         max_size=computation WS+
         paragraph WS*
      R_PAREN
   {
      $result =
         PromptInteger.build
         (
            PARSER.get_origin_at
            (
               ($PROMPT_INTEGER_KW.getLine()),
               ($PROMPT_INTEGER_KW.getCharPositionInLine())
            ),
            ($targetv.result),
            ($min_size.result),
            ($max_size.result),
            ($paragraph.result)
         );
   }

/******************************************************************************/
/**** GENERIC INSTRUCTIONS ****************************************************/
/******************************************************************************/
   | L_PAREN identifier IMP_MARKER maybe_computation_list WS* R_PAREN
   {
      $result =
         GenericInstruction.build
         (
            PARSER.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($identifier.result),
            ($maybe_computation_list.result)
         );
   }

/******************************************************************************/
/**** DISPLAYED COMPUTATIONS **************************************************/
/******************************************************************************/
   | paragraph
   {
      $result =
         new Display
         (
            ($paragraph.result.get_origin()),
            ($paragraph.result)
         );
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

instruction_cond_list
returns [List<Cons<Computation, Instruction>> result]
@init
{
   Computation condition;

   condition = null;

   $result = new ArrayList<Cons<Computation, Instruction>>();
}
:
   (
      (
         (
            (L_PAREN WS* computation WS+)
            {
               condition = ($computation.result);
            }
         )
         |
         (
            something_else=.
            {
               condition =
                  VariableFromWord.generate
                  (
                     PARSER,
                     PARSER.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     ),
                     ($something_else.text).substring(1).trim()
                  );
            }
         )
      )
      {
         PARSER.increase_local_variables_hierarchy();
      }
         instruction WS* R_PAREN
      {
         PARSER.decrease_local_variables_hierarchy();

         $result.add(new Cons(condition, ($instruction.result)));
      }
      WS*
   )+
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

instruction_switch_list
returns [List<Cons<Computation, Instruction>> result]
@init
{
   $result = new ArrayList<Cons<Computation, Instruction>>();
}
:
   (
      L_PAREN WS* computation WS+
         {
            PARSER.increase_local_variables_hierarchy();
         }
         instruction WS* R_PAREN
      {
         PARSER.decrease_local_variables_hierarchy();

         $result.add
         (
            new Cons(($computation.result), ($instruction.result))
         );
      }
      WS*
   )+
   {
   }
;

player_choice_list [ParserData.PlayerChoiceData pcd]
returns [List<Instruction> result]
@init
{
   $result = new ArrayList<Instruction>();
}
:
   (
      WS* player_choice[pcd]
      {
         $result.add($player_choice.result);
      }
   )+
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

maybe_player_choice_list [ParserData.PlayerChoiceData pcd]
returns [List<Instruction> result]
@init
{
   $result = new ArrayList<Instruction>();
}
:
   (WS*
      player_choice[pcd]
      {
         $result.add(($player_choice.result));
      }
   WS*)*
   {
   }
;

player_choice [ParserData.PlayerChoiceData pcd]
returns [Instruction result]
/*
 * Do not use a separate Local Variable stack for the player choice
 * instructions but do have one for any variable defined in 'for' or 'foreach'
 * choices.
 */
:
   TEXT_OPTION_KW
      L_PAREN WS*
      {
         PARSER.enable_restricted_variable_stack_of(pcd);
      }
      paragraph
      WS* R_PAREN WS*
      {
         PARSER.disable_restricted_stack_of(pcd);
         PARSER.increase_local_variables_hierarchy();
      }
      maybe_instruction_list WS*
   R_PAREN
   {
      PARSER.decrease_local_variables_hierarchy();

      $result =
         new TextOption
         (
            PARSER.get_origin_at
            (
               ($TEXT_OPTION_KW.getLine()),
               ($TEXT_OPTION_KW.getCharPositionInLine())
            ),
            ($paragraph.result),
            ($maybe_instruction_list.result)
         );
   }

   | EVENT_OPTION_KW
      {
         PARSER.enable_restricted_variable_stack_of(pcd);
      }
      L_PAREN WS* WORD maybe_computation_list WS* R_PAREN WS*
      {
         PARSER.disable_restricted_stack_of(pcd);
         PARSER.increase_local_variables_hierarchy();
      }
      maybe_instruction_list WS*
   R_PAREN
   {
      final Origin origin;
      final Event event;

      PARSER.decrease_local_variables_hierarchy();

      origin =
         PARSER.get_origin_at
         (
            ($L_PAREN.getLine()),
            ($L_PAREN.getCharPositionInLine())
         );

      event = PARSER.get_world().events().get(origin, ($WORD.text));

      $result =
         EventOption.build
         (
            origin,
            event,
            ($maybe_computation_list.result),
            ($maybe_instruction_list.result)
         );
   }

   | L_PAREN maybe_player_choice_list[pcd] WS* R_PAREN
   {
      $result =
         new InstructionList
         (
            PARSER.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($maybe_player_choice_list.result)
         );
   }

   | IF_KW
         {
            PARSER.enable_restricted_variable_stack_of(pcd);
         }
         computation WS*
         {
            PARSER.disable_restricted_stack_of(pcd);
         }
         player_choice_list[pcd] WS*
      R_PAREN
   {
      $result =
         IfInstruction.build
         (
            PARSER.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($player_choice_list.result)
         );
   }

   | IF_ELSE_KW
         {
            PARSER.enable_restricted_variable_stack_of(pcd);
         }
         computation WS*
         {
            PARSER.disable_restricted_stack_of(pcd);
         }
         if_true=player_choice[pcd] WS*
         if_false=player_choice[pcd] WS*
      R_PAREN
   {
      $result =
         IfElseInstruction.build
         (
            PARSER.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | COND_KW player_choice_cond_list[pcd] WS* R_PAREN
   {
      $result =
         CondInstruction.build
         (
            PARSER.get_origin_at
            (
               ($COND_KW.getLine()),
               ($COND_KW.getCharPositionInLine())
            ),
            ($player_choice_cond_list.result)
         );
   }

   | SWITCH_KW
         {
            PARSER.enable_restricted_variable_stack_of(pcd);
         }
         computation WS*
         {
            PARSER.disable_restricted_stack_of(pcd);
         }
         player_choice_switch_list[pcd] WS+
         player_choice[pcd] WS*
      R_PAREN
   {
      $result =
         SwitchInstruction.build
         (
            PARSER.get_origin_at
            (
               ($SWITCH_KW.getLine()),
               ($SWITCH_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($player_choice_switch_list.result),
            ($player_choice.result)
         );
   }

   | FOR_KW
         l0=L_PAREN
         {
            PARSER.enable_restricted_variable_stack_of(pcd);
            PARSER.increase_local_variables_hierarchy();
            pcd.increase_variable_names_hierarchy();
         }
         choice_for_variable_list[pcd] WS*
         R_PAREN WS*
         computation WS*
         l1=L_PAREN
         choice_for_update_variable_list[pcd] WS*
         R_PAREN WS*
         {
            PARSER.disable_restricted_stack_of(pcd);
         }
         player_choice_list[pcd] WS*
      R_PAREN
   {
      pcd.decrease_variable_names_hierarchy();
      PARSER.enable_restricted_variable_stack_of(pcd);
      PARSER.decrease_local_variables_hierarchy();
      PARSER.disable_restricted_stack_of(pcd);

      $result =
         For.build
         (
            PARSER.get_origin_at
            (
               ($FOR_KW.getLine()),
               ($FOR_KW.getCharPositionInLine())
            ),
            ($computation.result),
            new InstructionList
            (
               PARSER.get_origin_at
               (
                  ($l0.getLine()),
                  ($l0.getCharPositionInLine())
               ),
               ($choice_for_variable_list.result)
            ),
            ($player_choice_list.result),
            new InstructionList
            (
               PARSER.get_origin_at
               (
                  ($l1.getLine()),
                  ($l1.getCharPositionInLine())
               ),
               ($choice_for_update_variable_list.result)
            )
         );
   }

   | FOR_EACH_KW
         {
            PARSER.enable_restricted_variable_stack_of(pcd);
            PARSER.increase_local_variables_hierarchy();
         }
         computation WS+
         {
         }
      identifier
      {
         final Map<String, Variable> variable_map;
         final Variable new_variable;
         final Type collection_type;
         Type elem_type;

         elem_type = Type.ANY;

         collection_type = ($computation.result).get_type();

         if (collection_type instanceof CollectionType)
         {
            elem_type = ((CollectionType) collection_type).get_content_type();
         }
         else
         {
            ErrorManager.handle
            (
               new InvalidTypeException
               (
                  PARSER.get_origin_at
                  (
                     ($FOR_EACH_KW.getLine()),
                     ($FOR_EACH_KW.getCharPositionInLine())
                  ),
                  elem_type,
                  Type.COLLECTION_TYPES
               )
            );

            elem_type = Type.ANY;
         }

         new_variable =
            new Variable
            (
               PARSER.get_origin_at
               (
                  ($FOR_EACH_KW.getLine()),
                  ($FOR_EACH_KW.getCharPositionInLine())
               ),
               elem_type,
               ($identifier.result),
               false
            );

         PARSER.add_local_variable(new_variable);
         PARSER.disable_restricted_stack_of(pcd);
      }
      WS+
      player_choice_list[pcd] WS*
   R_PAREN
   {
      PARSER.enable_restricted_variable_stack_of(pcd);
      PARSER.decrease_local_variables_hierarchy();
      PARSER.disable_restricted_stack_of(pcd);
      $result =
         new ForEach
         (
            PARSER.get_origin_at
            (
               ($FOR_EACH_KW.getLine()),
               ($FOR_EACH_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($identifier.result),
            ($player_choice_list.result)
         );
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

player_choice_cond_list [ParserData.PlayerChoiceData pcd]
returns [List<Cons<Computation, Instruction>> result]
@init
{
   Computation condition;

   condition = null;

   $result = new ArrayList<Cons<Computation, Instruction>>();
   PARSER.enable_restricted_variable_stack_of(pcd);
}
:
   (
      (
         (
            (L_PAREN WS* computation WS+)
            {
               condition = ($computation.result);

               PARSER.disable_restricted_stack_of(pcd);
            }
         )
         |
         (
            something_else=.
            {
               condition =
                  VariableFromWord.generate
                  (
                     PARSER,
                     PARSER.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     ),
                     ($something_else.text).substring(1).trim()
                  );

               PARSER.disable_restricted_stack_of(pcd);
            }
         )
      )
      player_choice[pcd] WS* R_PAREN
      {
         $result.add(new Cons(condition, ($player_choice.result)));
      }
      WS*
   )+
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

player_choice_switch_list [ParserData.PlayerChoiceData pcd]
returns [List<Cons<Computation, Instruction>> result]
@init
{
   $result = new ArrayList<Cons<Computation, Instruction>>();
   PARSER.enable_restricted_variable_stack_of(pcd);
}
:
   (
      L_PAREN
         WS* computation
         {
            PARSER.disable_restricted_stack_of(pcd);
         }
         WS* player_choice[pcd] WS*
      R_PAREN
      {
         $result.add(new Cons(($computation.result), ($player_choice.result)));
      }
      WS*
   )+
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

paragraph
returns [Computation result]
@init
{
}
:
   computation_list
   {
      // convert all computations to text.
      // return text node.
      $result =
         new Paragraph
         (
            $computation_list.result.get(0).get_origin(),
            $computation_list.result
         );
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

sentence
returns [Computation result]
@init
{
   final StringBuilder string_builder = new StringBuilder();
}
:
   first_word=WORD
   {
      string_builder.append(($first_word.text));
   }
   (
      WS+ next_word=WORD
      {
         string_builder.append(" ");
         string_builder.append(($next_word.text));
      }
   )*
   {
      $result =
         Constant.build_string
         (
            PARSER.get_origin_at
            (
               ($first_word.getLine()),
               ($first_word.getCharPositionInLine())
            ),
            string_builder.toString()
         );
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

type
returns [Type result]
:
   WORD
   {
      $result =
         PARSER.get_world().types().get
         (
            PARSER.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      if ($result.get_parameters().size() != 0)
      {
         // TODO: throw error.
      }
   }

   | LAMBDA_KW type WS* L_PAREN maybe_type_list R_PAREN WS* R_PAREN
   {
      final Type t;

      t =
         new LambdaType
         (
            PARSER.get_origin_at
            (
               ($LAMBDA_KW.getLine()),
               ($LAMBDA_KW.getCharPositionInLine())
            ),
            ($type.result),
            "autogenerated lambda type",
            ($maybe_type_list.result)
         );
   }

   | SEQUENCE_KW maybe_type_list R_PAREN
   {
      final Type t;

      t =
         new SequenceType
         (
            PARSER.get_origin_at
            (
               ($SEQUENCE_KW.getLine()),
               ($SEQUENCE_KW.getCharPositionInLine())
            ),
            "autogenerated sequence type",
            ($maybe_type_list.result)
         );
   }

   | L_PAREN identifier WS+ type_list WS* R_PAREN
   {
      final Origin origin;
      final Type t;

      origin =
         PARSER.get_origin_at
         (
            ($L_PAREN.getLine()),
            ($L_PAREN.getCharPositionInLine())
         );

      t = PARSER.get_world().types().get(origin, ($identifier.result));

      $result = t.generate_variant(origin, $type_list.result);
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

type_list
returns [List<Type> result]
@init
{
   $result = new ArrayList<Type>();
}
:
   type
   {
      $result.add(($type.result));
   }
   (WS+
      type
      {
         $result.add(($type.result));
      }
   )*
   {
   }
;

maybe_type_list
returns [List<Type> result]
@init
{
   $result = new ArrayList<Type>();
}
:
   (WS+
      type
      {
         $result.add(($type.result));
      }
   )*
   {
   }
;

let_variable_list
returns [List<Cons<Variable, Computation>> result]
@init
{
   String var_name;

   var_name = null;

   $result = new ArrayList<Cons<Variable, Computation>>();
}
:
   (
      WS*
         (
            (
               L_PAREN WS* identifier
               {
                  var_name = ($identifier.result);
               }
            )
            |
            (
               something_else=.
               {
                  var_name = ($something_else.text).substring(1).trim();
               }
            )
         )
         WS+ computation WS* R_PAREN
      {
         final Variable v;

         v =
            new Variable
            (
               PARSER.get_origin_at
               (
                  ($L_PAREN.getLine()),
                  ($L_PAREN.getCharPositionInLine())
               ),
               ($computation.result).get_type(),
               var_name,
               false
            );

         PARSER.add_local_variable(v);

         $result.add(new Cons(v, ($computation.result)));
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

choice_for_update_variable_list [ParserData.PlayerChoiceData pcd]
returns [List<Instruction> result]
@init
{
   String var_name;
   Origin origin;

   var_name = null;
   origin = null;

   $result = new ArrayList<Instruction>();
}
:
   (
      WS*
         (
            (
               L_PAREN WS* identifier
               {
                  var_name = ($identifier.result);
                  origin =
                     PARSER.get_origin_at
                     (
                        ($L_PAREN.getLine()),
                        ($L_PAREN.getCharPositionInLine())
                     );
               }
            )
            |
            (
               something_else=.
               {
                  var_name = ($something_else.text).substring(1).trim();
                  origin =
                     PARSER.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     );
               }
            )
         )
         WS+ computation WS* R_PAREN
      {
         $result.add
         (
            SetValue.build
            (
               origin,
               ($computation.result),
               VariableFromWord.generate
               (
                  PARSER,
                  origin,
                  var_name
               )
            )
         );

         if (!pcd.can_update_variable(var_name))
         {
            ErrorManager.handle
            (
               new UpdatingIllegalVariableFromChoiceException(origin, var_name)
            );
         }
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

choice_for_variable_list [ParserData.PlayerChoiceData pcd]
returns [List<Instruction> result]
@init
{
   String var_name;
   Origin origin;

   var_name = null;
   origin = null;

   $result = new ArrayList<Instruction>();
}
:
   (
      WS*
         (
            (
               L_PAREN WS* identifier
               {
                  var_name = ($identifier.result);
                  origin =
                     PARSER.get_origin_at
                     (
                        ($L_PAREN.getLine()),
                        ($L_PAREN.getCharPositionInLine())
                     );
               }
            )
            |
            (
               something_else=.
               {
                  var_name = ($something_else.text).substring(1).trim();
                  origin =
                     PARSER.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     );
               }
            )
         )
         WS+ computation WS* R_PAREN
      {
         final Variable new_var;

         new_var =
            new Variable
            (
               origin,
               ($computation.result).get_type(),
               var_name,
               false
            );

         $result.add(new LocalVariable(new_var));

         PARSER.add_local_variable(new_var);

         $result.add
         (
            SetValue.build
            (
               origin,
               ($computation.result),
               VariableFromWord.generate(PARSER, origin, var_name)
            )
         );

         pcd.mark_name_as_editable(var_name);
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

variable_list
returns [VariableList result]
@init
{
   Type next_type;
   Origin origin;

   next_type = null;
   origin = null;

   $result = new VariableList();
}
:
   (
      WS*
      (
         (
            (
               L_PAREN WS* type WS+
            )
            {
               origin =
                  PARSER.get_origin_at
                  (
                     ($L_PAREN.getLine()),
                     ($L_PAREN.getCharPositionInLine())
                  );
               next_type = ($type.result);
            }
         )
         |
         (
            something_else=.
            {
               origin =
                  PARSER.get_origin_at
                  (
                     ($something_else.getLine()),
                     ($something_else.getCharPositionInLine())
                  );

               next_type =
                  PARSER.get_world().types().get
                  (
                     origin,
                     ($something_else.text).substring(1).trim()
                  );
            }
         )
      )
      WS* identifier WS* R_PAREN
      {
         $result.add
         (
            new Variable
            (
               origin,
               next_type,
               ($identifier.result),
               false
            )
         );
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

field_value_list
returns [List<Cons<Origin, Cons<String, Computation>>> result]
@init
{
   String field_name;

   field_name = null;

   $result = new ArrayList<Cons<Origin, Cons<String, Computation>>>();
}
:
   (
      WS*
      (
         (
            L_PAREN WS* WORD WS+
            {
               field_name = ($WORD.text);
            }
         )
         |
         (
            something_else=.
            {
               field_name = ($something_else.text).substring(1).trim();
            }
         )
      )
      computation WS* R_PAREN
      {
         $result.add
         (
            new Cons
            (
               PARSER.get_origin_at
               (
                  ($L_PAREN.getLine()),
                  ($L_PAREN.getCharPositionInLine())
               ),
               new Cons(field_name, ($computation.result))
            )
         );
      }
   )*
   {
   }
;

identifier
returns [String result]
:
   WORD
   {
      if (($WORD.text).indexOf('.') != -1)
      {
         ErrorManager.handle
         (
            new IllegalReferenceNameException
            (
               PARSER.get_origin_at
               (
                  ($WORD.getLine()),
                  ($WORD.getCharPositionInLine())
               ),
               ($WORD.text)
            )
         );
      }

      $result = ($WORD.text);
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

/******************************************************************************/
/**** VALUES ******************************************************************/
/******************************************************************************/

computation
returns [Computation result]
@init
{
   ParserData.LocalVariables previous_local_variables_stack;
}
:
   WORD
   {
      if ($WORD.text.matches("[0-9]+(\.[0-9]+)?"))
      {
         return
            Constant.build
            (
               PARSER.get_origin_at
               (
                  ($WORD.getLine()),
                  ($WORD.getCharPositionInLine())
               ),
               ($WORD.text)
            );
      }
      else
      {
         $result =
            new AmbiguousWord
            (
               PARSER,
               PARSER.get_origin_at
               (
                  ($WORD.getLine()),
                  ($WORD.getCharPositionInLine())
               ),
               ($WORD.text)
            );
      }
   }

   | VARIABLE_KW WORD WS* R_PAREN
   {
      $result =
         VariableFromWord.generate
         (
            PARSER,
            PARSER.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );
   }

   | IGNORE_ERROR_KW WORD WS+ computation WS* R_PAREN
   {
      $result = ($computation.result);
      /* TODO: temporarily disable an compiler error category */
   }

   | STRING_KW sentence WS* R_PAREN
   {
      $result = ($sentence.result);
   }

   | L_PAREN WS+ sentence WS* R_PAREN
   {
      $result = ($sentence.result);
   }

   | FIELD_ACCESS_KW WORD WS+ computation WS* R_PAREN
   {
      $result =
         FieldAccess.build
         (
            PARSER.get_origin_at
            (
               ($FIELD_ACCESS_KW.getLine()),
               ($FIELD_ACCESS_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($WORD.text)
         );
   }

   | DEFAULT_KW type WS* R_PAREN
   {
      $result =
         new Default
         (
            PARSER.get_origin_at
            (
               ($DEFAULT_KW.getLine()),
               ($DEFAULT_KW.getCharPositionInLine())
            ),
            ($type.result)
         );
   }

   | COND_KW computation_cond_list WS* R_PAREN
   {
      $result =
         CondValue.build
         (
            PARSER.get_origin_at
            (
               ($COND_KW.getLine()),
               ($COND_KW.getCharPositionInLine())
            ),
            ($computation_cond_list.result)
         );
   }

   | SWITCH_KW
         target=computation WS*
         computation_switch_list WS*
         default_val=computation WS*
      R_PAREN
   {
      $result =
         SwitchValue.build
         (
            PARSER.get_origin_at
            (
               ($SWITCH_KW.getLine()),
               ($SWITCH_KW.getCharPositionInLine())
            ),
            ($target.result),
            ($computation_switch_list.result),
            ($default_val.result)
         );
   }

   | LAMBDA_KW
      {
         previous_local_variables_stack = PARSER.get_local_variables_stack();
         PARSER.discard_local_variables_stack();
         PARSER.increase_local_variables_hierarchy();
      }
         L_PAREN WS* variable_list WS* R_PAREN
         {
            PARSER.add_local_variables(($variable_list.result).as_map());
         }
         WS*
         computation
         WS*
      R_PAREN
      {
         PARSER.restore_local_variables_stack(previous_local_variables_stack);

         $result =
            LambdaExpression.build
            (
               PARSER.get_origin_at
               (
                  ($LAMBDA_KW.getLine()),
                  ($LAMBDA_KW.getCharPositionInLine())
               ),
               ($variable_list.result).get_entries(),
               ($computation.result)
            );
      }

   | LET_KW
      {
         PARSER.increase_local_variables_hierarchy();
      }
         L_PAREN WS* let_variable_list WS* R_PAREN
         WS*
         computation
         WS*
      R_PAREN
      {
         final List<Cons<Variable, Computation>> let_list;

         PARSER.decrease_local_variables_hierarchy();

         let_list = ($let_variable_list.result);

         $result =
            new Let
            (
               PARSER.get_origin_at
               (
                  ($LET_KW.getLine()),
                  ($LET_KW.getCharPositionInLine())
               ),
               let_list,
               ($computation.result)
            );
      }

   | CAST_KW type WS+ computation WS* R_PAREN
   {
      $result =
         Cast.build
         (
            PARSER.get_origin_at
            (
               ($CAST_KW.getLine()),
               ($CAST_KW.getCharPositionInLine())
            ),
            $type.result,
            ($computation.result),
            false
         );
   }

   | SET_FIELDS_KW computation WS* field_value_list WS* R_PAREN
   {
      $result =
         new SetFieldsComputation
         (
            PARSER.get_origin_at
            (
               ($SET_FIELDS_KW.getLine()),
               ($SET_FIELDS_KW.getCharPositionInLine())
            ),
            ($computation.result),
            ($field_value_list.result)
         );
   }

   | ENABLE_TEXT_EFFECT_KW WORD WS+ paragraph WS* R_PAREN
   {
      final TextEffect effect;

      effect =
         PARSER.get_world().text_effects().get
         (
            PARSER.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      $result =
         TextWithEffect.build
         (
            PARSER.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            effect,
            new ArrayList<Computation>(),
            ($paragraph.result)
         );
   }

   | ENABLE_TEXT_EFFECT_KW
      L_PAREN
         WORD WS+
         computation_list WS*
      R_PAREN WS+
      paragraph WS*
      R_PAREN
   {
      final TextEffect effect;

      effect =
         PARSER.get_world().text_effects().get
         (
            PARSER.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      $result =
         TextWithEffect.build
         (
            PARSER.get_origin_at
            (
               ($ENABLE_TEXT_EFFECT_KW.getLine()),
               ($ENABLE_TEXT_EFFECT_KW.getCharPositionInLine())
            ),
            effect,
            ($computation_list.result),
            ($paragraph.result)
         );
   }

   | L_PAREN identifier maybe_computation_list WS* R_PAREN
   {
      $result =
         GenericComputation.build
         (
            PARSER.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($identifier.result),
            ($maybe_computation_list.result)
         );
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

computation_cond_list
returns [List<Cons<Computation, Computation>> result]
@init
{
   Computation condition;

   condition = null;

   $result = new ArrayList<Cons<Computation, Computation>>();
}
:
   (
      (
         (
            (
               L_PAREN WS* c=computation WS+
            )
            {
               condition = ($c.result);
            }
         )
         |
         (
            something_else=.
            {
               condition =
                  VariableFromWord.generate
                  (
                     PARSER,
                     PARSER.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     ),
                     ($something_else.text).substring(1).trim()
                  );
            }
         )
      )
      v=computation WS* R_PAREN WS*
      {
         $result.add(new Cons(condition, ($v.result)));
      }
   )+
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

computation_switch_list
returns [List<Cons<Computation, Computation>> result]
@init
{
   $result = new ArrayList<Cons<Computation, Computation>>();
}
:
   (
      L_PAREN WS* c=computation WS+ v=computation WS* R_PAREN WS*
      {
         $result.add(new Cons(($c.result), ($v.result)));
      }
   )+
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

maybe_computation_list
returns [List<Computation> result]
@init
{
   $result = new ArrayList<Computation>();
}
:
   (WS+
      computation
      {
         ($result).add(($computation.result));
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

computation_list
returns [List<Computation> result]
@init
{
   $result = new ArrayList<Computation>();
}
:
   computation
   {
      ($result).add(($computation.result));
   }
   (WS+
      computation
      {
         ($result).add(($computation.result));
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}
