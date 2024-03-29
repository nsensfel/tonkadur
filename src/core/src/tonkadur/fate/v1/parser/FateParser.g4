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

   import tonkadur.error.ErrorLevel;
   import tonkadur.error.ErrorCategory;
   import tonkadur.error.ErrorManager;

   import tonkadur.functional.Cons;

   import tonkadur.parser.Context;
   import tonkadur.parser.Location;
   import tonkadur.parser.Origin;
   import tonkadur.parser.BasicParsingError;

/* Conflicts with an existing package */
/*   import tonkadur.fate.v1.tonkadur.fate.v1.Utils; */

   import tonkadur.fate.v1.error.DuplicateLocalVariableException;
   import tonkadur.fate.v1.error.IllegalReferenceNameException;
   import tonkadur.fate.v1.error.InvalidArityException;
   import tonkadur.fate.v1.error.InvalidTypeException;
   import tonkadur.fate.v1.error.InvalidTypeArityException;
   import tonkadur.fate.v1.error.UpdatingIllegalVariableFromChoiceException;
   import tonkadur.fate.v1.error.UnknownExtensionContentException;

   import tonkadur.fate.v1.lang.*;
   import tonkadur.fate.v1.lang.instruction.*;
   import tonkadur.fate.v1.lang.meta.*;
   import tonkadur.fate.v1.lang.type.*;
   import tonkadur.fate.v1.lang.computation.*;

   import tonkadur.fate.v1.lang.computation.generic.ExtraComputation;
   import tonkadur.fate.v1.lang.computation.generic.UserComputation;

   import tonkadur.fate.v1.lang.computation.generic.Newline;

   import tonkadur.fate.v1.lang.instruction.generic.SetValue;
   import tonkadur.fate.v1.lang.instruction.generic.ExtraInstruction;
   import tonkadur.fate.v1.lang.instruction.generic.UserInstruction;
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
   WS* FATE_VERSION_KW word WS* R_PAREN WS*
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

      RecurrentChecks.assert_has_user_content_prefix
      (
         start_origin,
         ($identifier.result)
      );
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

      RecurrentChecks.assert_has_user_content_prefix
      (
         start_origin,
         ($identifier.result)
      );
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

      ExtraInstruction.register
      (
         ($identifier.result),
         ($maybe_type_list.result)
      );

      RecurrentChecks.assert_has_user_content_prefix
      (
         start_origin,
         ($identifier.result)
      );
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

      ExtraComputation.register
      (
         ($type.result),
         ($identifier.result),
         ($maybe_type_list.result)
      );

      RecurrentChecks.assert_has_user_content_prefix
      (
         start_origin,
         ($identifier.result)
      );
   }

   | DECLARE_EXTRA_TYPE_KW identifier WS+ argc=word WS+ comp=word WS* R_PAREN
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
            ErrorManager.handle
            (
               new BasicParsingError
               (
                  ErrorLevel.FATAL,
                  ErrorCategory.INVALID_INPUT,
                  start_origin.with_hint("second parameter"),
                  "An integer higher than zero is needed here."
               )
            );
         }
      }
      catch (final Exception e)
      {
         arg_count = 0;

         ErrorManager.handle
         (
            new BasicParsingError
            (
               ErrorLevel.FATAL,
               ErrorCategory.INVALID_INPUT,
               start_origin.with_hint("second parameter"),
               "An integer higher than zero is needed here."
            )
         );

         throw e;
      }

      try
      {
         is_comparable = Boolean.parseBoolean($comp.text);
      }
      catch (final Exception e)
      {
         is_comparable = false;

         ErrorManager.handle
         (
            new BasicParsingError
            (
               ErrorLevel.FATAL,
               ErrorCategory.INVALID_INPUT,
               start_origin.with_hint("third parameter"),
               "An boolean is needed here."
            )
         );

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

      RecurrentChecks.assert_has_user_content_prefix
      (
         start_origin,
         ($identifier.result)
      );
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

   | DECLARE_GLOBAL_VARIABLE_KW
         type WS+
         name=identifier WS+
         value=computation[true] WS*
      R_PAREN
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

      PARSER.get_world().add_global_instruction
      (
         SetValue.build
         (
            start_origin,
            new VariableReference(start_origin, new_variable),
            ($value.result)
         )
      );
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

   | DECLARE_INSTRUCTION_KW
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
            ($DECLARE_INSTRUCTION_KW.getLine()),
            ($DECLARE_INSTRUCTION_KW.getCharPositionInLine())
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

      PARSER.restore_local_variables_stack(previous_local_variables_stack);

      UserInstruction.register(new_sequence);

      RecurrentChecks.assert_has_user_content_prefix
      (
         start_origin,
         ($identifier.result)
      );
   }

   | DECLARE_COMPUTATION_KW
      {
         previous_local_variables_stack = PARSER.get_local_variables_stack();
         PARSER.discard_local_variables_stack();
         PARSER.increase_local_variables_hierarchy();
      }
         identifier WS*
         L_PAREN WS* variable_list WS* R_PAREN
         {
            PARSER.add_local_variables(($variable_list.result).as_map());
         }
         WS*
         computation[true]
         WS*
      R_PAREN
      {
         final Origin start_origin;
         final LambdaExpression computation;

         PARSER.restore_local_variables_stack(previous_local_variables_stack);

         start_origin =
            PARSER.get_origin_at
            (
               ($DECLARE_COMPUTATION_KW.getLine()),
               ($DECLARE_COMPUTATION_KW.getCharPositionInLine())
            );

         computation =
            LambdaExpression.build
            (
               start_origin,
               ($variable_list.result).get_entries(),
               ($computation.result)
            );

         UserComputation.register(($identifier.result), computation);

         RecurrentChecks.assert_has_user_content_prefix
         (
            start_origin,
            ($identifier.result)
         );
      }

   | IMP_IGNORE_ERROR_KW word WS+ first_level_instruction WS* R_PAREN
   {
      /* TODO: temporarily disable an compiler error category */
   }


   | REQUIRE_EXTENSION_KW word WS* R_PAREN
   {
      PARSER.get_world().add_required_extension(($word.result));

      /* TODO: error report if extension not explicitly enabled. */
   }

   | REQUIRE_KW word WS* R_PAREN
   {
      final String filename;

      filename = Files.resolve_filename(PARSER.get_context(), ($word.result));

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

   | INCLUDE_KW word WS* R_PAREN
   {
      final String filename;

      filename = Files.resolve_filename(PARSER.get_context(), ($word.result));

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
   | EXTENSION_FIRST_LEVEL_KW word WS+ instruction_list WS* R_PAREN
   {
      final Origin origin;
      final ExtensionInstruction instr;

      origin = ($word.origin);

      instr =
         PARSER.get_world().extension_first_level_instructions().get
         (
            ($word.result)
         );

      if (instr == null)
      {
         ErrorManager.handle
         (
            new UnknownExtensionContentException(origin, ($word.result))
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
   L_PAREN WS+ maybe_instruction_list WS* R_PAREN
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

   | DECLARE_LOCAL_VARIABLE_KW
      type WS+
      name=identifier WS+
      value=computation[true] WS*
      R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;
      final Map<String, Variable> variable_map;
      final List<Instruction> shorthand;

      shorthand = new ArrayList<Instruction>();

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

      shorthand.add(new LocalVariable(new_variable));
      shorthand.add
      (
         SetValue.build
         (
            start_origin,
            new VariableReference(start_origin, new_variable),
            ($value.result)
         )
      );

      $result = new InstructionList(start_origin, shorthand);
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

   | DO_WHILE_KW computation[true] WS*
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
         pre=instruction WS* computation[true] WS* post=instruction WS*
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

   | FOR_EACH_KW coll=computation[true] WS+ identifier
      {
         final Variable new_variable;
         final Type collection_type;
         Type elem_type;

         elem_type = Type.ANY;

         ($coll.result).expect_non_string();

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
         ForEach.build
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

   | WHILE_KW computation[true] WS*
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

   | SWITCH_KW computation[true] WS*
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
   | IF_KW computation[true] WS*
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

   | IF_ELSE_KW computation[true]
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

   | IMP_IGNORE_ERROR_KW word WS+ instruction WS* R_PAREN
   {
      /* TODO: temporarily disable an compiler error category */
      $result = ($instruction.result);
   }

   | IMP_ASSERT_KW computation[true] WS+ paragraph[false] WS* R_PAREN
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

   | IMP_SET_FIELDS_KW computation[true] WS* field_value_list WS* R_PAREN
   {
      $result =
         SetFields.build
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

   | PROMPT_COMMAND_KW
         targetv=computation[true] WS+
         min_size=computation[true] WS+
         max_size=computation[true] WS+
         paragraph[false] WS*
      R_PAREN
   {
      $result =
         PromptCommand.build
         (
            PARSER.get_origin_at
            (
               ($PROMPT_COMMAND_KW.getLine()),
               ($PROMPT_COMMAND_KW.getCharPositionInLine())
            ),
            ($targetv.result),
            ($min_size.result),
            ($max_size.result),
            ($paragraph.result)
         );
   }

   | PROMPT_STRING_KW
         targetv=computation[true] WS+
         min_size=computation[true] WS+
         max_size=computation[true] WS+
         paragraph[false] WS*
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

   | PROMPT_FLOAT_KW
         targetv=computation[true] WS+
         min_size=computation[true] WS+
         max_size=computation[true] WS+
         paragraph[false] WS*
      R_PAREN
   {
      $result =
         PromptFloat.build
         (
            PARSER.get_origin_at
            (
               ($PROMPT_FLOAT_KW.getLine()),
               ($PROMPT_FLOAT_KW.getCharPositionInLine())
            ),
            ($targetv.result),
            ($min_size.result),
            ($max_size.result),
            ($paragraph.result)
         );
   }

   | PROMPT_INTEGER_KW
         targetv=computation[true] WS+
         min_size=computation[true] WS+
         max_size=computation[true] WS+
         paragraph[false] WS*
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
/**** SEQUENCE CALL/JUMP ******************************************************/
/******************************************************************************/
   | VISIT_KW computation[true] maybe_computation_list WS* R_PAREN
   {
      final Origin origin;
      final Computation sequence;

      sequence = ($computation.result);
      origin =
         PARSER.get_origin_at
         (
            ($VISIT_KW.getLine()),
            ($VISIT_KW.getCharPositionInLine())
         );

      sequence.expect_string();

      if (sequence instanceof AmbiguousWord)
      {
         final String sequence_name;

         sequence_name = ((AmbiguousWord) sequence).get_value_as_string();

         PARSER.get_world().add_sequence_use
         (
            origin,
            sequence_name,
            ($maybe_computation_list.result)
         );

         $result =
            new SequenceCall
            (
               origin,
               sequence_name,
               ($maybe_computation_list.result)
            );
      }
      else
      {
         final String keyword;

         keyword = ($VISIT_KW.text).trim();
         $result =
            GenericInstruction.build
            (
               origin,
               keyword.substring(1, (keyword.length() - 1)),
               ($maybe_computation_list.result)
            );
      }
   }

   | CONTINUE_AS_KW computation[true] maybe_computation_list WS* R_PAREN
   {
      final Origin origin;
      final Computation sequence;

      sequence = ($computation.result);
      origin =
         PARSER.get_origin_at
         (
            ($CONTINUE_AS_KW.getLine()),
            ($CONTINUE_AS_KW.getCharPositionInLine())
         );

      sequence.expect_string();

      if (sequence instanceof AmbiguousWord)
      {
         final String sequence_name;

         sequence_name = ((AmbiguousWord) sequence).get_value_as_string();

         PARSER.get_world().add_sequence_use
         (
            origin,
            sequence_name,
            ($maybe_computation_list.result)
         );

         $result =
            new SequenceJump
            (
               origin,
               sequence_name,
               ($maybe_computation_list.result)
            );
      }
      else
      {
         final String keyword;

         keyword = ($CONTINUE_AS_KW.text).trim();
         $result =
            GenericInstruction.build
            (
               origin,
               keyword.substring(1, (keyword.length() - 1)),
               ($maybe_computation_list.result)
            );
      }
   }

/******************************************************************************/
/**** GENERIC INSTRUCTIONS ****************************************************/
/******************************************************************************/
   | L_PAREN WORD maybe_computation_list WS* R_PAREN
   {
      $result =
         GenericInstruction.build
         (
            PARSER.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($WORD.text).substring(0, (($WORD.text).length() - 1)),
            ($maybe_computation_list.result)
         );
   }

/******************************************************************************/
/**** DISPLAYED COMPUTATIONS **************************************************/
/******************************************************************************/
   | paragraph[true]
   {
      $result =
         Display.build
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
            (L_PAREN WS* computation[true] WS+)
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
      L_PAREN WS* computation[true] WS+
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
         //PARSER.enable_restricted_variable_stack_of(pcd);
      }
      paragraph[false]
      WS* R_PAREN WS*
      {
         //PARSER.disable_restricted_stack_of(pcd);
         PARSER.increase_local_variables_hierarchy();
      }
      maybe_instruction_list WS*
   R_PAREN
   {
      PARSER.decrease_local_variables_hierarchy();

      $result =
         TextOption.build
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
         //PARSER.enable_restricted_variable_stack_of(pcd);
      }
      L_PAREN WS* word maybe_computation_list WS* R_PAREN WS*
      {
         //PARSER.disable_restricted_stack_of(pcd);
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

      event = PARSER.get_world().events().get(origin, ($word.result));

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
           // PARSER.enable_restricted_variable_stack_of(pcd);
         }
         computation[true] WS*
         {
           // PARSER.disable_restricted_stack_of(pcd);
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
           // PARSER.enable_restricted_variable_stack_of(pcd);
         }
         computation[true] WS*
         {
           // PARSER.disable_restricted_stack_of(pcd);
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
            //PARSER.enable_restricted_variable_stack_of(pcd);
         }
         computation[true] WS*
         {
            //PARSER.disable_restricted_stack_of(pcd);
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
            //PARSER.enable_restricted_variable_stack_of(pcd);
            //PARSER.increase_local_variables_hierarchy();
            pcd.increase_variable_names_hierarchy();
         }
         choice_for_variable_list[pcd] WS*
         R_PAREN WS*
         computation[true] WS*
         l1=L_PAREN
         choice_for_update_variable_list[pcd] WS*
         R_PAREN WS*
         {
            //PARSER.disable_restricted_stack_of(pcd);
         }
         player_choice_list[pcd] WS*
      R_PAREN
   {
      pcd.decrease_variable_names_hierarchy();
      //PARSER.enable_restricted_variable_stack_of(pcd);
      //PARSER.decrease_local_variables_hierarchy();
      //PARSER.disable_restricted_stack_of(pcd);

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
            //PARSER.enable_restricted_variable_stack_of(pcd);
            PARSER.increase_local_variables_hierarchy();
         }
         computation[true] WS+
         {
         }
      identifier
      {
         final Map<String, Variable> variable_map;
         final Variable new_variable;
         final Type collection_type;
         Type elem_type;

         elem_type = Type.ANY;

         ($computation.result).expect_non_string();

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
         //PARSER.disable_restricted_stack_of(pcd);
      }
      WS+
      player_choice_list[pcd] WS*
   R_PAREN
   {
      //PARSER.enable_restricted_variable_stack_of(pcd);
      PARSER.decrease_local_variables_hierarchy();
      //PARSER.disable_restricted_stack_of(pcd);
      $result =
         ForEach.build
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
   //PARSER.enable_restricted_variable_stack_of(pcd);
}
:
   (
      (
         (
            (L_PAREN WS* computation[true] WS+)
            {
               condition = ($computation.result);

    //           PARSER.disable_restricted_stack_of(pcd);
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

    //           PARSER.disable_restricted_stack_of(pcd);
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
   //PARSER.enable_restricted_variable_stack_of(pcd);
}
:
   (
      L_PAREN
         WS* computation[true]
         {
    //        PARSER.disable_restricted_stack_of(pcd);
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

paragraph [boolean precede_by_space]
returns [Computation result]
@init
{
}
:
   computation_list_with_explicit_spaces
   {
      List<Computation> content;
      // convert all computations to text.
      // return text node.

      content = $computation_list_with_explicit_spaces.result;

      if (precede_by_space)
      {
         content.add(0, Constant.build_string(Origin.BASE_LANGUAGE, " "));
      }

      $result =
         Paragraph.build
         (
            $computation_list_with_explicit_spaces.result.get(0).get_origin(),
            content
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
   first_word=word
   {
      string_builder.append(($first_word.result));
   }
   (
      WS+ next_word=word
      {
         string_builder.append(" ");
         string_builder.append(($next_word.result));
      }
   )*
   {
      $result =
         Constant.build_string
         (
            ($first_word.origin),
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
   word
   {
      $result = PARSER.get_world().types().get(($word.origin), ($word.result));

      if ($result.get_parameters().size() != 0)
      {
         ErrorManager.handle
         (
            new InvalidTypeArityException(($word.origin), ($result))
         );
      }
   }

   | LAMBDA_KW type WS* L_PAREN no_space_maybe_type_list R_PAREN WS* R_PAREN
   {
      $result =
         new LambdaType
         (
            PARSER.get_origin_at
            (
               ($LAMBDA_KW.getLine()),
               ($LAMBDA_KW.getCharPositionInLine())
            ),
            ($type.result),
            "autogenerated lambda type",
            ($no_space_maybe_type_list.result)
         );
   }

   | SEQUENCE_KW no_space_maybe_type_list R_PAREN
   {
      $result =
         new SequenceType
         (
            PARSER.get_origin_at
            (
               ($SEQUENCE_KW.getLine()),
               ($SEQUENCE_KW.getCharPositionInLine())
            ),
            "autogenerated sequence type",
            ($no_space_maybe_type_list.result)
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

no_space_maybe_type_list
returns [List<Type> result]
@init
{
   $result = new ArrayList<Type>();
}
:
   (WS*
      type
      {
         $result.add(($type.result));
      }
   )*
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
         WS+ computation[true] WS* R_PAREN
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
         WS+ computation[true] WS* R_PAREN
      {
         $result.add
         (
            SetValue.build
            (
               origin,
               VariableFromWord.generate
               (
                  PARSER,
                  origin,
                  var_name
               ),
               ($computation.result)
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
         WS+ computation[true] WS* R_PAREN
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
               VariableFromWord.generate(PARSER, origin, var_name),
               ($computation.result)
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
            L_PAREN WS* word WS+
            {
               field_name = ($word.result);
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
      computation[true] WS* R_PAREN
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
   word
   {
      if (($word.result).indexOf('.') != -1)
      {
         ErrorManager.handle
         (
            new IllegalReferenceNameException(($word.origin), ($word.result))
         );
      }

      $result = ($word.result);
   }
;
catch [final Throwable e]
{
   PARSER.handle_error(e);
}

/******************************************************************************/
/**** VALUES ******************************************************************/
/******************************************************************************/

computation [boolean allows_var_shorthand]
returns [Computation result]
@init
{
   ParserData.LocalVariables previous_local_variables_stack;
}
:
   word
   {
      if
      (
         !allows_var_shorthand
         || $word.result.matches("(-?)[0-9]+(\\.[0-9]+)?")
      )
      {
         $result = Constant.build(($word.origin), ($word.result));
      }
      else
      {
         $result =
            new AmbiguousWord
            (
               PARSER,
               ($word.origin),
               ($word.result),
               VariableFromWord.attempt(PARSER, ($word.origin), ($word.result))
            );
      }
   }

   | VARIABLE_KW word WS* R_PAREN
   {
      $result =
         VariableFromWord.generate(PARSER, ($word.origin), ($word.result));
   }

   | IGNORE_ERROR_KW word WS+ computation[true] WS* R_PAREN
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

   | FIELD_ACCESS_KW word WS+ computation[true] WS* R_PAREN
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
            ($word.result)
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
         target=computation[true] WS*
         computation_switch_list WS*
         default_val=computation[true] WS*
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

   | SEQUENCE_KW identifier WS* R_PAREN
   {
      $result =
         new SequenceReference
         (
            PARSER.get_origin_at
            (
               ($SEQUENCE_KW.getLine()),
               ($SEQUENCE_KW.getCharPositionInLine())
            ),
            $identifier.text
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
         computation[true]
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
         computation[true]
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

   | CAST_KW type WS+ computation[true] WS* R_PAREN
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

   | SET_FIELDS_KW computation[true] WS* field_value_list WS* R_PAREN
   {
      $result =
         SetFieldsComputation.build
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

   | TEXT_KW paragraph[false] WS* R_PAREN
   {
      $result = ($paragraph.result);
   }

   | ENABLE_TEXT_EFFECT_KW word WS+ paragraph[false] WS* R_PAREN
   {
      final TextEffect effect;

      effect =
         PARSER.get_world().text_effects().get(($word.origin), ($word.result));

      $result =
         TextWithEffect.build
         (
            ($word.origin),
            effect,
            new ArrayList<Computation>(),
            ($paragraph.result)
         );
   }

   | ENABLE_TEXT_EFFECT_KW
      L_PAREN
         word WS+
         computation_list WS*
      R_PAREN WS+
      paragraph[false] WS*
      R_PAREN
   {
      final TextEffect effect;

      effect =
         PARSER.get_world().text_effects().get(($word.origin), ($word.result));

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

   | L_PAREN IDENTIFIER_KW maybe_computation_list WS* R_PAREN
   {
      $result =
         GenericComputation.build
         (
            PARSER.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($IDENTIFIER_KW.text),
            ($maybe_computation_list.result)
         );
   }

   | IF_KW computation_list WS* R_PAREN
   {
      $result =
         GenericComputation.build
         (
            PARSER.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($IF_KW.text).substring(1).trim(),
            ($computation_list.result)
         );
   }

   | IF_ELSE_KW computation_list WS* R_PAREN
   {
      $result =
         GenericComputation.build
         (
            PARSER.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($IF_ELSE_KW.text).substring(1).trim(),
            ($computation_list.result)
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
               L_PAREN WS* c=computation[true] WS+
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
      v=computation[true] WS* R_PAREN WS*
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
      L_PAREN WS* c=computation[true] WS+ v=computation[true] WS* R_PAREN WS*
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
      computation[true]
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
   computation[true]
   {
      ($result).add(($computation.result));
   }
   (WS+
      computation[true]
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

computation_list_with_explicit_spaces
returns [List<Computation> result]
@init
{
   $result = new ArrayList<Computation>();
   boolean follows_newline, just_added_space;

   just_added_space = false;
}
:
   computation[false]
   {
      if (($computation.result) instanceof Newline)
      {
         follows_newline = true;
      }
      else
      {
         follows_newline = false;
      }

      just_added_space = false;

      ($result).add(($computation.result));
   }
   (
      (WS+
         {
            if (!follows_newline)
            {
               ($result).add
               (
                  Constant.build_string
                  (
                     PARSER.get_origin_at
                     (
                        ($WS.getLine()),
                        ($WS.getCharPositionInLine())
                     ),
                     " "
                  )
               );

               just_added_space = true;
            }
         }
      )*
      computation[false]
      {
         if (($computation.result) instanceof Newline)
         {
            follows_newline = true;

            if (just_added_space)
            {
               ($result).remove(($result).size() - 1);
            }
         }
         else
         {
            follows_newline = false;
         }

         just_added_space = false;

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

word returns [String result, Origin origin]:
   WORD
   {
      $result = $WORD.text;
      $origin =
         PARSER.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );
   }

   | IMP_MARKER
   {
      $result = "!";
      $origin =
         PARSER.get_origin_at
         (
            ($IMP_MARKER.getLine()),
            ($IMP_MARKER.getCharPositionInLine())
         );
   }

   | IDENTIFIER_KW
   {
      $result = $IDENTIFIER_KW.text;
      $origin =
         PARSER.get_origin_at
         (
            ($IDENTIFIER_KW.getLine()),
            ($IDENTIFIER_KW.getCharPositionInLine())
         );
   }
;
