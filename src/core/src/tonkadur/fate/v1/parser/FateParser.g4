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
   Context CONTEXT;
   World WORLD;
   Deque<Map<String, Variable>> LOCAL_VARIABLES;
   Deque<List<String>> HIERARCHICAL_VARIABLES;
   int BREAKABLE_LEVELS;
   Deque<Collection<String>> CHOICE_LIMITED_VARIABLES;
}

/******************************************************************************/
/******************************************************************************/
/******************************************************************************/
fate_file [Context context,  Deque<Map<String, Variable>> local_variables, World world]
   @init
   {
      CONTEXT = context;
      WORLD = world;

      if (local_variables == null)
      {
         LOCAL_VARIABLES = new ArrayDeque<Map<String, Variable>>();
         LOCAL_VARIABLES.push(new HashMap<String, Variable>());
      }
      else
      {
         LOCAL_VARIABLES = local_variables;
      }

      HIERARCHICAL_VARIABLES = new ArrayDeque<List<String>>();
      BREAKABLE_LEVELS = 0;

      HIERARCHICAL_VARIABLES.push(new ArrayList<String>());
      CHOICE_LIMITED_VARIABLES = new ArrayDeque<Collection<String>>();
   }
   :
   WS* FATE_VERSION_KW WORD WS* R_PAREN WS*
   (
      (
         first_level_fate_instr
         |
         (
            general_fate_instr
            {
               WORLD.add_global_instruction(($general_fate_instr.result));
            }
         )
      )
      WS*
   )*
   EOF
   {
   }
;

general_fate_sequence
returns [List<Instruction> result]
@init
{
   $result = new ArrayList<Instruction>();
}
:
   (WS*
      general_fate_instr
      {
         $result.add(($general_fate_instr.result));
      }
   WS*)*
   {
   }
;

first_level_fate_instr:
   DEFINE_SEQUENCE_KW
      new_reference_name
      WS*
      (
         L_PAREN WS* variable_list WS* R_PAREN
         {
            final Map<String, Variable> variable_map;

            variable_map = new HashMap<String, Variable>();

            variable_map.putAll(($variable_list.result).as_map());

            LOCAL_VARIABLES.push(variable_map);
         }
      )
      pre_sequence_point=WS+
      general_fate_sequence
      WS*
   R_PAREN
   {
      final Origin start_origin, sequence_origin;
      final Sequence new_sequence;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DEFINE_SEQUENCE_KW.getLine()),
            ($DEFINE_SEQUENCE_KW.getCharPositionInLine())
         );

      sequence_origin =
         CONTEXT.get_origin_at
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
               ($general_fate_sequence.result)
            ),
            ($new_reference_name.result),
            ($variable_list.result).get_entries()
         );

      WORLD.sequences().add(new_sequence);
      LOCAL_VARIABLES.pop();
   }

   | DECLARE_VARIABLE_KW
      type
      WS+
      name=new_reference_name
      WS*
   R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_VARIABLE_KW.getLine()),
            ($DECLARE_VARIABLE_KW.getCharPositionInLine())
         );

      new_variable =
         new Variable
         (
            start_origin,
            ($type.result),
            ($name.result),
            false
         );

      WORLD.variables().add(new_variable);
   }

   | EXTERNAL_KW
      type
      WS+
      name=new_reference_name
      WS*
   R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($EXTERNAL_KW.getLine()),
            ($EXTERNAL_KW.getCharPositionInLine())
         );

      new_variable =
         new Variable
         (
            start_origin,
            ($type.result),
            ($name.result),
            true
         );

      WORLD.variables().add(new_variable);
   }

   | IGNORE_ERROR_KW WORD WS+ first_level_fate_instr WS* R_PAREN
   {
      /* TODO: temporarily disable an compiler error category */
   }

   | DECLARE_TEXT_EFFECT_KW
      new_reference_name
      WS+
      params=type_list
      WS*
   R_PAREN
   {
      final Origin start_origin;
      final TextEffect new_text_effect;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_TEXT_EFFECT_KW.getLine()),
            ($DECLARE_TEXT_EFFECT_KW.getCharPositionInLine())
         );

      new_text_effect =
         new TextEffect
         (
            start_origin,
            ($type_list.result),
            ($new_reference_name.result)
         );

      WORLD.text_effects().add(new_text_effect);
   }

   | DECLARE_TEXT_EFFECT_KW
      new_reference_name
      WS*
   R_PAREN
   {
      final Origin start_origin;
      final TextEffect new_text_effect;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_TEXT_EFFECT_KW.getLine()),
            ($DECLARE_TEXT_EFFECT_KW.getCharPositionInLine())
         );

      new_text_effect =
         new TextEffect
         (
            start_origin,
            new ArrayList(),
            ($new_reference_name.result)
         );

      WORLD.text_effects().add(new_text_effect);
   }

   | REQUIRE_EXTENSION_KW WORD WS* R_PAREN
   {
      WORLD.add_required_extension(($WORD.text));

      /* TODO: error report if extension not explicitly enabled. */
   }

   | DECLARE_ALIAS_TYPE_KW parent=type WS+ new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final Type new_type;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_ALIAS_TYPE_KW.getLine()),
            ($DECLARE_ALIAS_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         ($parent.result).generate_alias
         (
            start_origin,
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_DICT_TYPE_KW
      new_reference_name
      WS*
      variable_list
      WS*
   R_PAREN
   {
      final Origin start_origin;
      final Type new_type;
      final Map<String, Type> field_types;

      field_types = new HashMap<String, Type>();

      for
      (
         final Variable te:
            ($variable_list.result).get_entries()
      )
      {
         field_types.put(te.get_name(), te.get_type());
      }

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_DICT_TYPE_KW.getLine()),
            ($DECLARE_DICT_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         new DictType
         (
            start_origin,
            field_types,
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_EXTRA_INSTRUCTION_KW new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final ExtraInstruction extra_instruction;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EXTRA_INSTRUCTION_KW.getLine()),
            ($DECLARE_EXTRA_INSTRUCTION_KW.getCharPositionInLine())
         );

      extra_instruction =
         new ExtraInstruction
         (
            start_origin,
            ($new_reference_name.result),
            new ArrayList<Type>()
         );

      WORLD.extra_instructions().add(extra_instruction);
   }

   | DECLARE_EXTRA_INSTRUCTION_KW new_reference_name WS+ type_list WS* R_PAREN
   {
      final Origin start_origin;
      final ExtraInstruction extra_instruction;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EXTRA_INSTRUCTION_KW.getLine()),
            ($DECLARE_EXTRA_INSTRUCTION_KW.getCharPositionInLine())
         );

      extra_instruction =
         new ExtraInstruction
         (
            start_origin,
            ($new_reference_name.result),
            ($type_list.result)
         );

      WORLD.extra_instructions().add(extra_instruction);
   }

   | DECLARE_EXTRA_COMPUTATION_KW type WS+ new_reference_name WS+ type_list WS* R_PAREN
   {
      final Origin start_origin;
      final ExtraComputation extra_computation;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EXTRA_COMPUTATION_KW.getLine()),
            ($DECLARE_EXTRA_COMPUTATION_KW.getCharPositionInLine())
         );

      extra_computation =
         new ExtraComputation
         (
            start_origin,
            ($type.result),
            ($new_reference_name.result),
            ($type_list.result)
         );

      WORLD.extra_computations().add(extra_computation);
   }

   | DECLARE_EXTRA_COMPUTATION_KW type WS+ new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final ExtraComputation extra_computation;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EXTRA_COMPUTATION_KW.getLine()),
            ($DECLARE_EXTRA_COMPUTATION_KW.getCharPositionInLine())
         );

      extra_computation =
         new ExtraComputation
         (
            start_origin,
            ($type.result),
            ($new_reference_name.result),
            new ArrayList<Type>()
         );

      WORLD.extra_computations().add(extra_computation);
   }

   | DECLARE_EVENT_TYPE_KW new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final Event new_event;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EVENT_TYPE_KW.getLine()),
            ($DECLARE_EVENT_TYPE_KW.getCharPositionInLine())
         );

      new_event =
         new Event
         (
            start_origin,
            new ArrayList<Type>(),
            ($new_reference_name.result)
         );

      WORLD.events().add(new_event);
   }

   | DECLARE_EVENT_TYPE_KW new_reference_name WS+ type_list WS* R_PAREN
   {
      final Origin start_origin;
      final Event new_event;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EVENT_TYPE_KW.getLine()),
            ($DECLARE_EVENT_TYPE_KW.getCharPositionInLine())
         );

      new_event =
         new Event
         (
            start_origin,
            ($type_list.result),
            ($new_reference_name.result)
         );

      WORLD.events().add(new_event);
   }


   | REQUIRE_KW WORD WS* R_PAREN
   {
      final String filename;

      filename = Files.resolve_filename(CONTEXT, ($WORD.text));

      if (!WORLD.has_loaded_file(filename))
      {
         CONTEXT.push
         (
            CONTEXT.get_location_at
            (
               ($REQUIRE_KW.getLine()),
               ($REQUIRE_KW.getCharPositionInLine())
            ),
            filename
         );

         tonkadur.fate.v1.Utils.add_file_content
         (
            filename,
            CONTEXT,
            LOCAL_VARIABLES,
            WORLD
         );

         CONTEXT.pop();
      }
   }

   | INCLUDE_KW WORD WS* R_PAREN
   {
      final String filename;

      filename = Files.resolve_filename(CONTEXT, ($WORD.text));

      CONTEXT.push
      (
         CONTEXT.get_location_at
         (
            ($INCLUDE_KW.getLine()),
            ($INCLUDE_KW.getCharPositionInLine())
         ),
         filename
      );

      tonkadur.fate.v1.Utils.add_file_content
      (
         filename,
         CONTEXT,
         LOCAL_VARIABLES,
         WORLD
      );

      CONTEXT.pop();
   }

   /*
   | EXTENSION_FIRST_LEVEL_KW WORD WS+ general_fate_sequence WS* R_PAREN
   {
      final Origin origin;
      final ExtensionInstruction instr;

      origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      instr = WORLD.extension_first_level_instructions().get(($WORD.text));

      if (instr == null)
      {
         ErrorManager.handle
         (
            new UnknownExtensionContentException(origin, ($WORD.text))
         );
      }
      else
      {
         instr.build(WORLD, CONTEXT, origin, ($general_fate_sequence.result));
      }
   }
   */
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

/* Trying to get rule priorities right */
general_fate_instr
returns [Instruction result]
:
   L_PAREN WS+ general_fate_sequence WS+ R_PAREN
   {
      $result =
         new InstructionList
         (
            CONTEXT.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($general_fate_sequence.result)
         );
   }

   | LOCAL_KW
      type
      WS+
      name=new_reference_name
      WS*
   R_PAREN
   {
      final Origin start_origin, type_origin;
      final Variable new_variable;
      final Map<String, Variable> variable_map;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($LOCAL_KW.getLine()),
            ($LOCAL_KW.getCharPositionInLine())
         );

      new_variable =
         new Variable
         (
            start_origin,
            ($type.result),
            ($name.result),
            false
         );

      variable_map = LOCAL_VARIABLES.peekFirst();

      if (variable_map.containsKey(($name.result)))
      {
         ErrorManager.handle
         (
            new DuplicateLocalVariableException
            (
               variable_map.get(($name.result)),
               new_variable
            )
         );
      }
      else
      {
         variable_map.put(($name.result), new_variable);
      }

      $result = new LocalVariable(new_variable);

      if (!HIERARCHICAL_VARIABLES.isEmpty())
      {
         HIERARCHICAL_VARIABLES.peekFirst().add(new_variable.get_name());
      }
   }

   | PROMPT_STRING_KW
         targetv=non_text_value WS+
         min_size=non_text_value WS+
         max_size=non_text_value WS+
         paragraph WS*
      R_PAREN
   {
      $result =
         PromptString.build
         (
            CONTEXT.get_origin_at
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
         targetv=non_text_value WS+
         min_size=non_text_value WS+
         max_size=non_text_value WS+
         paragraph WS*
      R_PAREN
   {
      $result =
         PromptInteger.build
         (
            CONTEXT.get_origin_at
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

   | IMP_ADD_KW value_list WS+ value_reference WS* R_PAREN
   {
      final List<Instruction> add_instrs;

      add_instrs = new ArrayList<Instruction>();


      for (final Computation value: ($value_list.result))
      {
         add_instrs.add
         (
            AddElement.build
            (
               CONTEXT.get_origin_at
               (
                  ($IMP_ADD_KW.getLine()),
                  ($IMP_ADD_KW.getCharPositionInLine())
               ),
               value,
               ($value_reference.result)
            )
         );
      }

      $result =
         new InstructionList
         (
            CONTEXT.get_origin_at
            (
               ($IMP_ADD_KW.getLine()),
               ($IMP_ADD_KW.getCharPositionInLine())
            ),
            add_instrs
         );
   }

   | IMP_ADD_AT_KW
         index=non_text_value WS+
         element=value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         AddElementAt.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_ADD_AT_KW.getLine()),
               ($IMP_ADD_AT_KW.getCharPositionInLine())
            ),
            ($index.result),
            ($element.result),
            ($value_reference.result)
         );
   }

   | IMP_ADD_ALL_KW
         sourcev=non_text_value WS+
         target=value_reference WS*
      R_PAREN
   {
      $result =
         AddElementsOf.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_ADD_ALL_KW.getLine()),
               ($IMP_ADD_ALL_KW.getCharPositionInLine())
            ),
            ($sourcev.result),
            ($target.result)
         );
   }

   | END_KW
   {
      $result =
         new End
         (
            CONTEXT.get_origin_at
            (
               ($END_KW.getLine()),
               ($END_KW.getCharPositionInLine())
            )
         );
   }

   | DONE_KW
   {
      $result =
         new Done
         (
            CONTEXT.get_origin_at
            (
               ($DONE_KW.getLine()),
               ($DONE_KW.getCharPositionInLine())
            )
         );
   }

   | IGNORE_ERROR_KW WORD WS+ general_fate_instr WS* R_PAREN
   {
      /* TODO: temporarily disable an compiler error category */
      $result = ($general_fate_instr.result);
   }

   | IMP_REMOVE_ONE_KW
         value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         RemoveElement.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_REMOVE_ONE_KW.getLine()),
               ($IMP_REMOVE_ONE_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | IMP_REMOVE_AT_KW
         non_text_value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         RemoveElementAt.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_REMOVE_AT_KW.getLine()),
               ($IMP_REMOVE_AT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result)
         );
   }

   | IMP_REMOVE_ALL_KW
         value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         RemoveAllOfElement.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_REMOVE_ALL_KW.getLine()),
               ($IMP_REMOVE_ALL_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | CLEAR_KW value_reference WS* R_PAREN
   {
      $result =
         Clear.build
         (
            CONTEXT.get_origin_at
            (
               ($CLEAR_KW.getLine()),
               ($CLEAR_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | IMP_REVERSE_KW value_reference WS* R_PAREN
   {
      $result =
         ReverseList.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_REVERSE_KW.getLine()),
               ($IMP_REVERSE_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | IMP_PUSH_LEFT_KW value WS+ value_reference WS* R_PAREN
   {
      $result =
         PushElement.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_PUSH_LEFT_KW.getLine()),
               ($IMP_PUSH_LEFT_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result),
            true
         );
   }

   | IMP_PUSH_RIGHT_KW value WS+ value_reference WS* R_PAREN
   {
      $result =
         PushElement.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_PUSH_RIGHT_KW.getLine()),
               ($IMP_PUSH_RIGHT_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result),
            false
         );
   }

   | IMP_POP_RIGHT_KW value_reference WS+ non_text_value WS* R_PAREN
   {
      $result =
         PopElement.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_POP_RIGHT_KW.getLine()),
               ($IMP_POP_RIGHT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            false
         );
   }

   | IMP_POP_LEFT_KW value_reference WS+ non_text_value WS* R_PAREN
   {
      $result =
         PopElement.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_POP_LEFT_KW.getLine()),
               ($IMP_POP_LEFT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            true
         );
   }

   | IMP_MAP_KW non_text_value WS+ value_reference WS* R_PAREN
   {
      $result =
         tonkadur.fate.v1.lang.instruction.Map.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_MAP_KW.getLine()),
               ($IMP_MAP_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            new ArrayList()
         );
   }

   | IMP_MAP_KW non_text_value WS+ value_reference WS+ value_list WS* R_PAREN
   {
      $result =
         tonkadur.fate.v1.lang.instruction.Map.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_MAP_KW.getLine()),
               ($IMP_MAP_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            ($value_list.result)
         );
   }

   | IMP_INDEXED_MAP_KW non_text_value WS+ value_reference WS* R_PAREN
   {
      $result =
         IndexedMap.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_MAP_KW.getLine()),
               ($IMP_INDEXED_MAP_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            new ArrayList()
         );
   }

   | IMP_INDEXED_MAP_KW
         non_text_value WS+
         value_reference WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMap.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_MAP_KW.getLine()),
               ($IMP_INDEXED_MAP_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            ($value_list.result)
         );
   }

   | IMP_MERGE_KW
         fun=non_text_value WS+
         inv1=non_text_value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         Merge.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_MERGE_KW.getLine()),
               ($IMP_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            null,
            ($value_reference.result),
            null,
            new ArrayList()
         );
   }

   | IMP_MERGE_KW
         fun=non_text_value WS+
         inv1=non_text_value WS+
         value_reference WS+
         value_list WS*
      R_PAREN
   {
      $result =
         Merge.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_MERGE_KW.getLine()),
               ($IMP_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            null,
            ($value_reference.result),
            null,
            ($value_list.result)
         );
   }

   | IMP_INDEXED_MERGE_KW
         fun=non_text_value WS+
         inv1=non_text_value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         IndexedMerge.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_MERGE_KW.getLine()),
               ($IMP_INDEXED_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            null,
            ($value_reference.result),
            null,
            new ArrayList()
         );
   }

   | IMP_INDEXED_MERGE_KW
         fun=non_text_value WS+
         inv1=non_text_value WS+
         value_reference WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMerge.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_MERGE_KW.getLine()),
               ($IMP_INDEXED_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            null,
            ($value_reference.result),
            null,
            ($value_list.result)
         );
   }

   | SAFE_IMP_MERGE_KW
      fun=non_text_value WS+
      def1=value WS+
      inv1=non_text_value WS+
      def0=value WS+
      value_reference WS*
      R_PAREN
   {
      $result =
         Merge.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_IMP_MERGE_KW.getLine()),
               ($SAFE_IMP_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            ($def1.result),
            ($value_reference.result),
            ($def0.result),
            new ArrayList()
         );
   }

   | SAFE_IMP_MERGE_KW
      fun=non_text_value WS+
      def1=value WS+
      inv1=non_text_value WS+
      def0=value WS+
      value_reference WS+
      value_list WS*
      R_PAREN
   {
      $result =
         Merge.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_IMP_MERGE_KW.getLine()),
               ($SAFE_IMP_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            ($def1.result),
            ($value_reference.result),
            ($def0.result),
            ($value_list.result)
         );
   }

   | SAFE_IMP_INDEXED_MERGE_KW
      fun=non_text_value WS+
      def1=value WS+
      inv1=non_text_value WS+
      def0=value WS+
      value_reference WS*
      R_PAREN
   {
      $result =
         IndexedMerge.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_IMP_INDEXED_MERGE_KW.getLine()),
               ($SAFE_IMP_INDEXED_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            ($def1.result),
            ($value_reference.result),
            ($def0.result),
            new ArrayList()
         );
   }

   | SAFE_IMP_INDEXED_MERGE_KW
      fun=non_text_value WS+
      def1=value WS+
      inv1=non_text_value WS+
      def0=value WS+
      value_reference WS+
      value_list WS*
      R_PAREN
   {
      $result =
         IndexedMerge.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_IMP_INDEXED_MERGE_KW.getLine()),
               ($SAFE_IMP_INDEXED_MERGE_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv1.result),
            ($def1.result),
            ($value_reference.result),
            ($def0.result),
            ($value_list.result)
         );
   }

   | IMP_SUB_LIST_KW
         vstart=non_text_value WS+
         vend=non_text_value WS+
         value_reference WS*
      R_PAREN
   {
      $result =
         SubList.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_SUB_LIST_KW.getLine()),
               ($IMP_SUB_LIST_KW.getCharPositionInLine())
            ),
            ($vstart.result),
            ($vend.result),
            ($value_reference.result)
         );
   }

   | IMP_FILTER_KW non_text_value WS+ value_reference WS* R_PAREN
   {
      $result =
         Filter.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_FILTER_KW.getLine()),
               ($IMP_FILTER_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            new ArrayList()
         );
   }

   | IMP_FILTER_KW non_text_value WS+ value_reference WS+ value_list WS* R_PAREN
   {
      $result =
         Filter.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_FILTER_KW.getLine()),
               ($IMP_FILTER_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            ($value_list.result)
         );
   }

   | IMP_INDEXED_FILTER_KW non_text_value WS+ value_reference WS* R_PAREN
   {
      $result =
         IndexedFilter.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_FILTER_KW.getLine()),
               ($IMP_INDEXED_FILTER_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            new ArrayList()
         );
   }

   | IMP_INDEXED_FILTER_KW
         non_text_value WS+
         value_reference WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedFilter.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_FILTER_KW.getLine()),
               ($IMP_INDEXED_FILTER_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            ($value_list.result)
         );
   }

   | IMP_PARTITION_KW
      non_text_value WS+
      iftrue=value_reference WS+
      iffalse=value_reference WS*
      R_PAREN
   {
      $result =
         Partition.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_PARTITION_KW.getLine()),
               ($IMP_PARTITION_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($iftrue.result),
            ($iffalse.result),
            new ArrayList()
         );
   }

   | IMP_PARTITION_KW
      non_text_value WS+
      iftrue=value_reference WS+
      iffalse=value_reference WS+
      value_list WS*
      R_PAREN
   {
      $result =
         Partition.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_PARTITION_KW.getLine()),
               ($IMP_PARTITION_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($iftrue.result),
            ($iffalse.result),
            ($value_list.result)
         );
   }

   | IMP_INDEXED_PARTITION_KW
      non_text_value WS+
      iftrue=value_reference WS+
      iffalse=value_reference WS*
      R_PAREN
   {
      $result =
         IndexedPartition.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_PARTITION_KW.getLine()),
               ($IMP_INDEXED_PARTITION_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($iftrue.result),
            ($iffalse.result),
            new ArrayList()
         );
   }

   | IMP_INDEXED_PARTITION_KW
      non_text_value WS+
      iftrue=value_reference WS+
      iffalse=value_reference WS+
      value_list WS*
      R_PAREN
   {
      $result =
         IndexedPartition.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_INDEXED_PARTITION_KW.getLine()),
               ($IMP_INDEXED_PARTITION_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($iftrue.result),
            ($iffalse.result),
            ($value_list.result)
         );
   }

   | IMP_SORT_KW non_text_value WS+ value_reference WS* R_PAREN
   {
      $result =
        Sort.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_SORT_KW.getLine()),
               ($IMP_SORT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            new ArrayList()
         );
   }

   | IMP_SORT_KW non_text_value WS+ value_reference WS+ value_list WS* R_PAREN
   {
      $result =
         Sort.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_SORT_KW.getLine()),
               ($IMP_SORT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($value_reference.result),
            ($value_list.result)
         );
   }


   | IMP_SHUFFLE_KW value_reference WS* R_PAREN
   {
      $result =
         Shuffle.build
         (
            CONTEXT.get_origin_at
            (
               ($IMP_SHUFFLE_KW.getLine()),
               ($IMP_SHUFFLE_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | SET_KW value_reference WS+ value WS* R_PAREN
   {
      $result =
         SetValue.build
         (
            CONTEXT.get_origin_at
            (
               ($SET_KW.getLine()),
               ($SET_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | ALLOCATE_KW value_reference WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($ALLOCATE_KW.getLine()),
            ($ALLOCATE_KW.getCharPositionInLine())
         );

      $result = Allocate.build(origin, ($value_reference.result));
   }


   | FREE_KW value_reference WS* R_PAREN
   {
      $result =
         new Free
         (
            CONTEXT.get_origin_at
            (
               ($FREE_KW.getLine()),
               ($FREE_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | IMP_SET_FIELDS_KW value_reference WS* field_value_list WS* R_PAREN
   {
      /*
       * A bit of a lazy solution: build field references, then extract the data
       */
      final List<Cons<String, Computation>> assignments;

      assignments = new ArrayList<Cons<String, Computation>>();

      for
      (
         final Cons<Origin, Cons<String, Computation>> entry:
            ($field_value_list.result)
      )
      {
         final FieldReference fr;
         final Computation cp;

         fr =
            FieldReference.build
            (
               entry.get_car(),
               ($value_reference.result),
               entry.get_cdr().get_car()
            );

         cp = entry.get_cdr().get_cdr();

         RecurrentChecks.assert_can_be_used_as(cp, fr.get_type());

         assignments.add(new Cons(fr.get_field_name(), cp));
      }

      $result =
         new SetFields
         (
            CONTEXT.get_origin_at
            (
               ($IMP_SET_FIELDS_KW.getLine()),
               ($IMP_SET_FIELDS_KW.getCharPositionInLine())
            ),
            ($value_reference.result),
            assignments
         );
   }

   | WHILE_KW non_text_value WS*
      {
         BREAKABLE_LEVELS++;
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         BREAKABLE_LEVELS--;

         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
      R_PAREN
   {
      $result =
         While.build
         (
            CONTEXT.get_origin_at
            (
               ($WHILE_KW.getLine()),
               ($WHILE_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($general_fate_sequence.result)
         );
   }

   | DO_WHILE_KW non_text_value WS*
      {
         BREAKABLE_LEVELS++;
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         BREAKABLE_LEVELS--;

         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
      R_PAREN
   {
      $result =
         DoWhile.build
         (
            CONTEXT.get_origin_at
            (
               ($DO_WHILE_KW.getLine()),
               ($DO_WHILE_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($general_fate_sequence.result)
         );
   }

   | {BREAKABLE_LEVELS > 0}? BREAK_KW
   {
      $result =
         new Break
         (
            CONTEXT.get_origin_at
            (
               ($BREAK_KW.getLine()),
               ($BREAK_KW.getCharPositionInLine())
            )
         );
   }

   | FOR_KW pre=general_fate_instr WS*
      non_text_value WS*
      post=general_fate_instr WS*
      {
         BREAKABLE_LEVELS++;
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         BREAKABLE_LEVELS--;

         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS* R_PAREN
   {
      $result =
         For.build
         (
            CONTEXT.get_origin_at
            (
               ($FOR_KW.getLine()),
               ($FOR_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($pre.result),
            ($general_fate_sequence.result),
            ($post.result)
         );
   }

   | FOR_EACH_KW
      coll=non_text_value WS+ new_reference_name
      {
         final Map<String, Variable> variable_map;
         final Variable new_variable;
         final Type collection_type;
         Type elem_type;

         elem_type = Type.ANY;

         collection_type = ($coll.result).get_type();

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
                  CONTEXT.get_origin_at
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
               CONTEXT.get_origin_at
               (
                  ($FOR_EACH_KW.getLine()),
                  ($FOR_EACH_KW.getCharPositionInLine())
               ),
               elem_type,
               ($new_reference_name.result),
               false
            );

         variable_map = LOCAL_VARIABLES.peekFirst();

         if (variable_map.containsKey(($new_reference_name.result)))
         {
            ErrorManager.handle
            (
               new DuplicateLocalVariableException
               (
                  variable_map.get(($new_reference_name.result)),
                  new_variable
               )
            );
         }
         else
         {
            variable_map.put(($new_reference_name.result), new_variable);
         }
      }
      WS+
      {
         BREAKABLE_LEVELS++;
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         BREAKABLE_LEVELS--;

         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
   R_PAREN
   {
      $result =
         new ForEach
         (
            CONTEXT.get_origin_at
            (
               ($FOR_EACH_KW.getLine()),
               ($FOR_EACH_KW.getCharPositionInLine())
            ),
            ($coll.result),
            ($new_reference_name.result),
            ($general_fate_sequence.result)
         );

      variable_map.remove(($new_reference_name.result));
   }

   | EXTRA_INSTRUCTION_KW WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final ExtraInstruction extra_instruction;

      origin =
         CONTEXT.get_origin_at
         (
            ($EXTRA_INSTRUCTION_KW.getLine()),
            ($EXTRA_INSTRUCTION_KW.getCharPositionInLine())
         );

      extra_instruction = WORLD.extra_instructions().get(origin, ($WORD.text));

      $result =
         extra_instruction.instantiate
         (
            WORLD,
            CONTEXT,
            origin,
            ($value_list.result)
         );
   }

   | EXTRA_INSTRUCTION_KW WORD WS* R_PAREN
   {
      final Origin origin;
      final ExtraInstruction extra_instruction;

      origin =
         CONTEXT.get_origin_at
         (
            ($EXTRA_INSTRUCTION_KW.getLine()),
            ($EXTRA_INSTRUCTION_KW.getCharPositionInLine())
         );

      extra_instruction = WORLD.extra_instructions().get(origin, ($WORD.text));

      $result =
         extra_instruction.instantiate
         (
            WORLD,
            CONTEXT,
            origin,
            new ArrayList<Computation>()
         );
   }

   | VISIT_KW WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final String sequence_name;

      origin =
         CONTEXT.get_origin_at
         (
            ($VISIT_KW.getLine()),
            ($VISIT_KW.getCharPositionInLine())
         );

      sequence_name = ($WORD.text);

      WORLD.add_sequence_use(origin, sequence_name, ($value_list.result));

      $result = new SequenceCall(origin, sequence_name, ($value_list.result));
   }

   | VISIT_KW WORD WS* R_PAREN
   {
      final Origin origin;
      final String sequence_name;
      final List<Computation> params;

      origin =
         CONTEXT.get_origin_at
         (
            ($VISIT_KW.getLine()),
            ($VISIT_KW.getCharPositionInLine())
         );

      params = new ArrayList<Computation>();

      sequence_name = ($WORD.text);

      WORLD.add_sequence_use(origin, sequence_name, params);

      $result = new SequenceCall(origin, sequence_name, params);
   }

   | CONTINUE_AS_KW WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final String sequence_name;

      origin =
         CONTEXT.get_origin_at
         (
            ($CONTINUE_AS_KW.getLine()),
            ($CONTINUE_AS_KW.getCharPositionInLine())
         );

      sequence_name = ($WORD.text);

      WORLD.add_sequence_use(origin, sequence_name, ($value_list.result));

      $result = new SequenceJump(origin, sequence_name, ($value_list.result));
   }

   | CONTINUE_AS_KW WORD WS* R_PAREN
   {
      final Origin origin;
      final String sequence_name;
      final List<Computation> params;

      origin =
         CONTEXT.get_origin_at
         (
            ($CONTINUE_AS_KW.getLine()),
            ($CONTINUE_AS_KW.getCharPositionInLine())
         );

      params = new ArrayList<Computation>();

      sequence_name = ($WORD.text);

      WORLD.add_sequence_use(origin, sequence_name, params);

      $result = new SequenceJump(origin, sequence_name, params);
   }

   | VISIT_KW value WS+ value_list WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($VISIT_KW.getLine()),
            ($VISIT_KW.getCharPositionInLine())
         );

      $result =
         SequenceVariableCall.build
         (
            origin,
            ($value.result),
            ($value_list.result)
         );
   }

   | VISIT_KW value WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($VISIT_KW.getLine()),
            ($VISIT_KW.getCharPositionInLine())
         );

      $result =
         SequenceVariableCall.build
         (
            origin,
            ($value.result),
            new ArrayList<Computation>()
         );
   }

   | CONTINUE_AS_KW value WS+ value_list WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($CONTINUE_AS_KW.getLine()),
            ($CONTINUE_AS_KW.getCharPositionInLine())
         );

      $result =
         SequenceVariableJump.build
         (
            origin,
            ($value.result),
            ($value_list.result)
         );
   }

   | CONTINUE_AS_KW value WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($CONTINUE_AS_KW.getLine()),
            ($CONTINUE_AS_KW.getCharPositionInLine())
         );

      $result =
         SequenceVariableJump.build
         (
            origin,
            ($value.result),
            new ArrayList<Computation>()
         );
   }

   | ASSERT_KW non_text_value WS+ paragraph WS* R_PAREN
   {
      $result =
         Assert.build
         (
            CONTEXT.get_origin_at
            (
               ($ASSERT_KW.getLine()),
               ($ASSERT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($paragraph.result)
         );
   }

   | IF_KW non_text_value WS*
      {
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS* R_PAREN
   {
      $result =
         IfInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($general_fate_sequence.result)
         );
   }

   | IF_ELSE_KW
         non_text_value
         {
            HIERARCHICAL_VARIABLES.push(new ArrayList());
         }
         WS+ if_true=general_fate_instr
         {
            for (final String s: HIERARCHICAL_VARIABLES.pop())
            {
               LOCAL_VARIABLES.peekFirst().remove(s);
            }

            HIERARCHICAL_VARIABLES.push(new ArrayList());
         }
         WS+ if_false=general_fate_instr
         {
            for (final String s: HIERARCHICAL_VARIABLES.pop())
            {
               LOCAL_VARIABLES.peekFirst().remove(s);
            }

         }
      WS* R_PAREN
   {
      $result =
         IfElseInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | COND_KW instr_cond_list WS* R_PAREN
   {
      $result =
         CondInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($COND_KW.getLine()),
               ($COND_KW.getCharPositionInLine())
            ),
            ($instr_cond_list.result)
         );
   }

   | SWITCH_KW non_text_value WS*
         instr_cond_list WS*
         general_fate_instr WS*
      R_PAREN
   {
      $result =
         SwitchInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($SWITCH_KW.getLine()),
               ($SWITCH_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($instr_cond_list.result),
            ($general_fate_instr.result)
         );
   }

   | PLAYER_CHOICE_KW
      {
         CHOICE_LIMITED_VARIABLES.push(new HashSet<String>());
      }
      player_choice_list WS* R_PAREN
   {
      $result =
         new PlayerChoice
         (
            CONTEXT.get_origin_at
            (
               ($PLAYER_CHOICE_KW.getLine()),
               ($PLAYER_CHOICE_KW.getCharPositionInLine())
            ),
            ($player_choice_list.result)
         );

      CHOICE_LIMITED_VARIABLES.pop();
   }

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
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

instr_cond_list
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
            (L_PAREN WS* non_text_value WS+)
            {
               condition = ($non_text_value.result);
            }
         )
         |
         (
            something_else=.
            {
               condition =
                  VariableFromWord.generate
                  (
                     WORLD,
                     LOCAL_VARIABLES,
                     CONTEXT.get_origin_at
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
            HIERARCHICAL_VARIABLES.push(new ArrayList());
         }
         general_fate_instr
         {
            for (final String s: HIERARCHICAL_VARIABLES.pop())
            {
               LOCAL_VARIABLES.peekFirst().remove(s);
            }
         }
         WS* R_PAREN
      {
         $result.add
         (
            new Cons(condition, ($general_fate_instr.result))
         );
      }
      WS*
   )+
   {
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

instr_switch_list
returns [List<Cons<Computation, Instruction>> result]
@init
{
   $result = new ArrayList<Cons<Computation, Instruction>>();
}
:
   (
      L_PAREN WS* value WS+
         {
            HIERARCHICAL_VARIABLES.push(new ArrayList());
         }
         general_fate_instr
         {
            for (final String s: HIERARCHICAL_VARIABLES.pop())
            {
               LOCAL_VARIABLES.peekFirst().remove(s);
            }
         }
         WS* R_PAREN
      {
         $result.add
         (
            new Cons(($value.result), ($general_fate_instr.result))
         );
      }
      WS*
   )+
   {
   }
;

player_choice_list
returns [List<Instruction> result]
@init
{
   $result = new ArrayList<Instruction>();
}
:
   (WS*
      player_choice
      {
         $result.add(($player_choice.result));
      }
   WS*)*
   {
   }
;

player_choice
returns [Instruction result]
:
   TEXT_OPTION_KW
      L_PAREN WS* paragraph WS* R_PAREN WS+
      {
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
   R_PAREN
   {
      $result =
         new TextOption
         (
            CONTEXT.get_origin_at
            (
               ($TEXT_OPTION_KW.getLine()),
               ($TEXT_OPTION_KW.getCharPositionInLine())
            ),
            ($paragraph.result),
            ($general_fate_sequence.result)
         );
   }

   | EVENT_OPTION_KW
      L_PAREN WS* WORD WS* R_PAREN WS+
      {
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
   R_PAREN
   {
      final Origin origin;
      final Event event;

      origin =
         CONTEXT.get_origin_at
         (
            ($L_PAREN.getLine()),
            ($L_PAREN.getCharPositionInLine())
         );

      event = WORLD.events().get(origin, ($WORD.text));

      $result =
         new EventOption
         (
            origin,
            event,
            ($general_fate_sequence.result)
         );
   }

   | EVENT_OPTION_KW
      L_PAREN WS* WORD WS+ value_list WS* R_PAREN WS+
      {
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      general_fate_sequence
      {
         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
   R_PAREN
   {
      final Origin origin;
      final Event event;

      origin =
         CONTEXT.get_origin_at
         (
            ($L_PAREN.getLine()),
            ($L_PAREN.getCharPositionInLine())
         );

      event = WORLD.events().get(origin, ($WORD.text));

      $result =
         EventOption.build
         (
            origin,
            event,
            ($value_list.result),
            ($general_fate_sequence.result)
         );
   }

   | IF_KW
         non_text_value WS+
         player_choice_list WS*
      R_PAREN
   {
      $result =
         IfInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($player_choice_list.result)
         );
   }

   | IF_ELSE_KW
      non_text_value WS+
      if_true=player_choice WS+
      if_false=player_choice WS*
   R_PAREN
   {
      $result =
         IfElseInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | COND_KW player_choice_cond_list WS* R_PAREN
   {
      $result =
         CondInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($COND_KW.getLine()),
               ($COND_KW.getCharPositionInLine())
            ),
            ($player_choice_cond_list.result)
         );
   }

   | SWITCH_KW value WS* player_choice_switch_list WS+ player_choice WS* R_PAREN
   {
      $result =
         SwitchInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($SWITCH_KW.getLine()),
               ($SWITCH_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($player_choice_switch_list.result),
            ($player_choice.result)
         );
   }

   | FOR_KW
      l0=L_PAREN
      choice_for_variable_list WS*
      R_PAREN WS*
      non_text_value WS*
      l1=L_PAREN
      choice_for_update_variable_list WS*
      R_PAREN WS*
      player_choice_list
      WS* R_PAREN
   {
      $result =
         For.build
         (
            CONTEXT.get_origin_at
            (
               ($FOR_KW.getLine()),
               ($FOR_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            new InstructionList
            (
               CONTEXT.get_origin_at
               (
                  ($l0.getLine()),
                  ($l0.getCharPositionInLine())
               ),
               ($choice_for_variable_list.result)
            ),
            ($player_choice_list.result),
            new InstructionList
            (
               CONTEXT.get_origin_at
               (
                  ($l1.getLine()),
                  ($l1.getCharPositionInLine())
               ),
               ($choice_for_update_variable_list.result)
            )
         );
   }

   | FOR_EACH_KW
      non_text_value WS+ new_reference_name
      {
         final Map<String, Variable> variable_map;
         final Variable new_variable;
         final Type collection_type;
         Type elem_type;

         elem_type = Type.ANY;

         collection_type = ($non_text_value.result).get_type();

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
                  CONTEXT.get_origin_at
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
               CONTEXT.get_origin_at
               (
                  ($FOR_EACH_KW.getLine()),
                  ($FOR_EACH_KW.getCharPositionInLine())
               ),
               elem_type,
               ($new_reference_name.result),
               false
            );

         variable_map = LOCAL_VARIABLES.peekFirst();

         if (variable_map.containsKey(($new_reference_name.result)))
         {
            ErrorManager.handle
            (
               new DuplicateLocalVariableException
               (
                  variable_map.get(($new_reference_name.result)),
                  new_variable
               )
            );
         }
         else
         {
            variable_map.put(($new_reference_name.result), new_variable);
         }
      }
      WS+
      {
         HIERARCHICAL_VARIABLES.push(new ArrayList());
      }
      player_choice_list
      {
         for (final String s: HIERARCHICAL_VARIABLES.pop())
         {
            LOCAL_VARIABLES.peekFirst().remove(s);
         }
      }
      WS*
   R_PAREN
   {
      $result =
         new ForEach
         (
            CONTEXT.get_origin_at
            (
               ($FOR_EACH_KW.getLine()),
               ($FOR_EACH_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($new_reference_name.result),
            ($player_choice_list.result)
         );

      variable_map.remove(($new_reference_name.result));
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

player_choice_cond_list
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
            (L_PAREN WS* non_text_value WS+)
            {
               condition = ($non_text_value.result);
            }
         )
         |
         (
            something_else=.
            {
               condition =
                  VariableFromWord.generate
                  (
                     WORLD,
                     LOCAL_VARIABLES,
                     CONTEXT.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     ),
                     ($something_else.text).substring(1).trim()
                  );
            }
         )
      )
      player_choice WS* R_PAREN
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
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

player_choice_switch_list
returns [List<Cons<Computation, Instruction>> result]
@init
{
   $result = new ArrayList<Cons<Computation, Instruction>>();
}
:
   (
      L_PAREN WS* value WS+ player_choice WS* R_PAREN
      {
         $result.add(new Cons(($value.result), ($player_choice.result)));
      }
      WS*
   )+
   {
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

paragraph
returns [TextNode result]
@init
{
   final List<TextNode> content = new ArrayList();
}
:
   first=text
   {
      content.add(($first.result));
   }
   (
      (WS+ next_a=text)
      {
         if (!(content.get(content.size() - 1) instanceof Newline))
         {
            content.add
            (
               ValueToText.build
               (
                  Constant.build_string
                  (
                     $next_a.result.get_origin(),
                     " "
                  )
               )
            );
         }

         content.add(($next_a.result));
      }
      |
      (next_b=text)
      {
         content.add(($next_b.result));
      }
   )*
   {
      if (content.size() == 1)
      {
         $result = content.get(0);
      }
      else
      {
         $result =
            new Paragraph
            (
               ($first.result.get_origin()),
               content
            );
      }
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

text
returns [TextNode result]:
   sentence
   {
      $result = ValueToText.build(($sentence.result));
   }

   | ENABLE_TEXT_EFFECT_KW WORD WS+ paragraph WS* R_PAREN
   {
      final TextEffect effect;

      effect =
         WORLD.text_effects().get
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      $result =
         TextWithEffect.build
         (
            CONTEXT.get_origin_at
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
         value_list WS*
      R_PAREN WS+
      paragraph WS*
      R_PAREN
   {
      final TextEffect effect;

      effect =
         WORLD.text_effects().get
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      $result =
         TextWithEffect.build
         (
            CONTEXT.get_origin_at
            (
               ($ENABLE_TEXT_EFFECT_KW.getLine()),
               ($ENABLE_TEXT_EFFECT_KW.getCharPositionInLine())
            ),
            effect,
            ($value_list.result),
            ($paragraph.result)
         );
   }

   | NEWLINE_KW
   {
      $result =
         new Newline
         (
            CONTEXT.get_origin_at
            (
               ($NEWLINE_KW.getLine()),
               ($NEWLINE_KW.getCharPositionInLine())
            )
         );
   }

   | non_text_value
   {
      $result = ValueToText.build(($non_text_value.result));
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

sentence
returns [Computation result]
@init
{
   final StringBuilder string_builder = new StringBuilder();
}
:
   STRING_KW sentence WS* R_PAREN
   {
      $result = ($sentence.result);
   }

   |
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
            CONTEXT.get_origin_at
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
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

type
returns [Type result]
:
   WORD
   {
      $result =
         WORLD.types().get
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );
   }

   | REF_KW type WS* R_PAREN
   {
      final Origin start_origin;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($REF_KW.getLine()),
            ($REF_KW.getCharPositionInLine())
         );

      $result =
         new PointerType
         (
            start_origin,
            ($type.result),
            ("anonymous (" + ($type.result).get_name() + ") ref type")
         );
   }

   | SET_KW type WS* R_PAREN
   {
      final Origin start_origin;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($SET_KW.getLine()),
            ($SET_KW.getCharPositionInLine())
         );

      $result =
         CollectionType.build
         (
            start_origin,
            ($type.result),
            true,
            ("anonymous (" + ($type.result).get_name() + ") set type")
         );
   }

   | LIST_KW type WS* R_PAREN
   {
      final Origin start_origin;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($LIST_KW.getLine()),
            ($LIST_KW.getCharPositionInLine())
         );

      $result =
         CollectionType.build
         (
            start_origin,
            ($type.result),
            false,
            ("anonymous (" + ($type.result).get_name() + ") list type")
         );
   }

   | CONS_KW t0=type WS+ t1=type WS* R_PAREN
   {
      $result =
         new ConsType
         (
            CONTEXT.get_origin_at
            (
               ($CONS_KW.getLine()),
               ($CONS_KW.getCharPositionInLine())
            ),
            ($t0.result),
            ($t1.result),
            ("anonymous (" + ($type.result).get_name() + ") list type")
         );
   }

   | LAMBDA_KW type WS* L_PAREN WS* type_list WS* R_PAREN WS* R_PAREN
   {
      final Origin start_origin;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($LAMBDA_KW.getLine()),
            ($LAMBDA_KW.getCharPositionInLine())
         );

      $result =
         new LambdaType
         (
            start_origin,
            ($type.result),
            "auto_generated",
            ($type_list.result)
         );
   }

   | SEQUENCE_KW type_list WS* R_PAREN
   {
      final Origin start_origin;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($SEQUENCE_KW.getLine()),
            ($SEQUENCE_KW.getCharPositionInLine())
         );

      $result =
         new SequenceType
         (
            start_origin,
            "auto_generated",
            ($type_list.result)
         );
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

type_list
returns [List<Type> result]
@init
{
   $result = new ArrayList<Type>();
}
:
   (
      type
      {
         $result.add(($type.result));
      }
   )?
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

   Map<String, Variable> variables;

   variables = LOCAL_VARIABLES.peekFirst();

   $result = new ArrayList<Cons<Variable, Computation>>();
}
:
   (
      WS*
         (
            (
               L_PAREN WS* new_reference_name
               {
                  var_name = ($new_reference_name.result);
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
         WS+ value WS* R_PAREN
      {
         final Variable v;

         v =
            new Variable
            (
               CONTEXT.get_origin_at
               (
                  ($L_PAREN.getLine()),
                  ($L_PAREN.getCharPositionInLine())
               ),
               ($value.result).get_type(),
               var_name,
               false
            );

         if (variables.containsKey(var_name))
         {
            ErrorManager.handle
            (
               new DuplicateLocalVariableException
               (
                  variables.get(var_name),
                  v
               )
            );
         }

         variables.put(var_name, v);

         $result.add(new Cons(v, ($value.result)));
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

choice_for_update_variable_list
returns [List<Instruction> result]
@init
{
   Collection<String> allowed_variables;
   String var_name;
   Origin origin;

   allowed_variables = CHOICE_LIMITED_VARIABLES.peek();
   var_name = null;
   origin = null;

   $result = new ArrayList<Instruction>();
}
:
   (
      WS*
         (
            (
               L_PAREN WS* new_reference_name
               {
                  var_name = ($new_reference_name.result);
                  origin =
                     CONTEXT.get_origin_at
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
                     CONTEXT.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     );
               }
            )
         )
         WS+ value WS* R_PAREN
      {
         $result.add
         (
            SetValue.build
            (
               origin,
               ($value.result),
               VariableFromWord.generate
               (
                  WORLD,
                  LOCAL_VARIABLES,
                  origin,
                  var_name
               )
            )
         );

         if (!allowed_variables.contains(var_name))
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
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

choice_for_variable_list
returns [List<Instruction> result]
@init
{
   Collection<String> allowed_variables;
   String var_name;
   Origin origin;

   allowed_variables = CHOICE_LIMITED_VARIABLES.peek();
   var_name = null;
   origin = null;

   $result = new ArrayList<Instruction>();
}
:
   (
      WS*
         (
            (
               L_PAREN WS* new_reference_name
               {
                  var_name = ($new_reference_name.result);
                  origin =
                     CONTEXT.get_origin_at
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
                     CONTEXT.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     );
               }
            )
         )
         WS+ value WS* R_PAREN
      {
         $result.add
         (
            SetValue.build
            (
               origin,
               ($value.result),
               VariableFromWord.generate
               (
                  WORLD,
                  LOCAL_VARIABLES,
                  origin,
                  var_name
               )
            )
         );

         allowed_variables.add(var_name);
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
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
                  CONTEXT.get_origin_at
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
                  CONTEXT.get_origin_at
                  (
                     ($something_else.getLine()),
                     ($something_else.getCharPositionInLine())
                  );

               next_type =
                  WORLD.types().get
                  (
                     origin,
                     ($something_else.text).substring(1).trim()
                  );
            }
         )
      )
      WS* new_reference_name WS* R_PAREN
      {
         $result.add
         (
            new Variable
            (
               origin,
               next_type,
               ($new_reference_name.result),
               false
            )
         );
      }
   )*
   {
   }
   |
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
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
      value WS* R_PAREN
      {
         $result.add
         (
            new Cons
            (
               CONTEXT.get_origin_at
               (
                  ($L_PAREN.getLine()),
                  ($L_PAREN.getCharPositionInLine())
               ),
               new Cons
               (
                  field_name,
                  ($value.result)
               )
            )
         );
      }
   )*
   {
   }
;

new_reference_name
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
               CONTEXT.get_origin_at
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
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

/******************************************************************************/
/**** VALUES ******************************************************************/
/******************************************************************************/
boolean_expression
returns [Computation result]:
   TRUE_KW
   {
      $result =
         Constant.build_boolean
         (
            CONTEXT.get_origin_at
            (
               ($TRUE_KW.getLine()),
               ($TRUE_KW.getCharPositionInLine())
            ),
            true
         );
   }

   | FALSE_KW
   {
      $result =
         Constant.build_boolean
         (
            CONTEXT.get_origin_at
            (
               ($FALSE_KW.getLine()),
               ($FALSE_KW.getCharPositionInLine())
            ),
            false
         );
   }

   | AND_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($AND_KW.getLine()),
               ($AND_KW.getCharPositionInLine())
            ),
            Operator.AND,
            ($non_text_value_list.result)
         );
   }

   | OR_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($OR_KW.getLine()),
               ($OR_KW.getCharPositionInLine())
            ),
            Operator.OR,
            ($non_text_value_list.result)
         );
   }

   | ONE_IN_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($ONE_IN_KW.getLine()),
               ($ONE_IN_KW.getCharPositionInLine())
            ),
            Operator.ONE_IN,
            ($non_text_value_list.result)
         );
   }

   | NOT_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($NOT_KW.getLine()),
               ($NOT_KW.getCharPositionInLine())
            ),
            Operator.NOT,
            ($non_text_value_list.result)
         );
   }

   | IMPLIES_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($IMPLIES_KW.getLine()),
               ($IMPLIES_KW.getCharPositionInLine())
            ),
            Operator.IMPLIES,
            ($non_text_value_list.result)
         );
   }

   | LOWER_THAN_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($LOWER_THAN_KW.getLine()),
               ($LOWER_THAN_KW.getCharPositionInLine())
            ),
            Operator.LOWER_THAN,
            ($non_text_value_list.result)
         );
   }

   | LOWER_EQUAL_THAN_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($LOWER_EQUAL_THAN_KW.getLine()),
               ($LOWER_EQUAL_THAN_KW.getCharPositionInLine())
            ),
            Operator.LOWER_EQUAL_THAN,
            ($non_text_value_list.result)
         );
   }

   | EQUALS_KW value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($EQUALS_KW.getLine()),
               ($EQUALS_KW.getCharPositionInLine())
            ),
            Operator.EQUALS,
            ($value_list.result)
         );
   }

   | GREATER_EQUAL_THAN_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($GREATER_EQUAL_THAN_KW.getLine()),
               ($GREATER_EQUAL_THAN_KW.getCharPositionInLine())
            ),
            Operator.GREATER_EQUAL_THAN,
            ($non_text_value_list.result)
         );
   }

   | GREATER_THAN_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($GREATER_THAN_KW.getLine()),
               ($GREATER_THAN_KW.getCharPositionInLine())
            ),
            Operator.GREATER_THAN,
            ($non_text_value_list.result)
         );
   }

   | IS_MEMBER_KW value WS+ value_reference WS* R_PAREN
   {
      $result =
         IsMemberOperator.build
         (
            CONTEXT.get_origin_at
            (
               ($IS_MEMBER_KW.getLine()),
               ($IS_MEMBER_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | IS_EMPTY_KW value_reference WS* R_PAREN
   {
      $result =
         IsEmpty.build
         (
            CONTEXT.get_origin_at
            (
               ($IS_EMPTY_KW.getLine()),
               ($IS_EMPTY_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | INDEX_OF_KW value WS+ value_reference WS* R_PAREN
   {
      $result =
         IndexOfOperator.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEX_OF_KW.getLine()),
               ($INDEX_OF_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | SIZE_KW value_reference WS* R_PAREN
   {
      $result =
         SizeOperator.build
         (
            CONTEXT.get_origin_at
            (
               ($SIZE_KW.getLine()),
               ($SIZE_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

math_expression
returns [Computation result]:
   PLUS_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($PLUS_KW.getLine()),
               ($PLUS_KW.getCharPositionInLine())
            ),
            Operator.PLUS,
            ($non_text_value_list.result)
         );
   }

   | MINUS_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($MINUS_KW.getLine()),
               ($MINUS_KW.getCharPositionInLine())
            ),
            Operator.MINUS,
            ($non_text_value_list.result)
         );
   }

   | MIN_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($MIN_KW.getLine()),
               ($MIN_KW.getCharPositionInLine())
            ),
            Operator.MIN,
            ($non_text_value_list.result)
         );
   }

   | MAX_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($MAX_KW.getLine()),
               ($MAX_KW.getCharPositionInLine())
            ),
            Operator.MAX,
            ($non_text_value_list.result)
         );
   }

   | CLAMP_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($CLAMP_KW.getLine()),
               ($CLAMP_KW.getCharPositionInLine())
            ),
            Operator.CLAMP,
            ($non_text_value_list.result)
         );
   }

   | ABS_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($ABS_KW.getLine()),
               ($ABS_KW.getCharPositionInLine())
            ),
            Operator.ABS,
            ($non_text_value_list.result)
         );
   }

   | MODULO_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($MODULO_KW.getLine()),
               ($MODULO_KW.getCharPositionInLine())
            ),
            Operator.MODULO,
            ($non_text_value_list.result)
         );
   }

   | TIMES_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($TIMES_KW.getLine()),
               ($TIMES_KW.getCharPositionInLine())
            ),
            Operator.TIMES,
            ($non_text_value_list.result)
         );
   }

   | DIVIDE_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($DIVIDE_KW.getLine()),
               ($DIVIDE_KW.getCharPositionInLine())
            ),
            Operator.DIVIDE,
            ($non_text_value_list.result)
         );
   }

   | POWER_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($POWER_KW.getLine()),
               ($POWER_KW.getCharPositionInLine())
            ),
            Operator.POWER,
            ($non_text_value_list.result)
         );
   }

   | RANDOM_KW non_text_value_list WS* R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($RANDOM_KW.getLine()),
               ($RANDOM_KW.getCharPositionInLine())
            ),
            Operator.RANDOM,
            ($non_text_value_list.result)
         );
   }

   | COUNT_KW value WS+ non_text_value WS* R_PAREN
   {
      $result =
         CountOperator.build
         (
            CONTEXT.get_origin_at
            (
               ($COUNT_KW.getLine()),
               ($COUNT_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($non_text_value.result)
         );
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

value
returns [Computation result]
:
   WORD
   {
      $result =
         Constant.build
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );
   }

   | IGNORE_ERROR_KW WORD WS+ value WS* R_PAREN
   {
      $result = ($value.result);
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

   | TEXT_KW paragraph WS* R_PAREN
   {
      $result = ($paragraph.result);
   }

   | ENABLE_TEXT_EFFECT_KW WORD WS+ paragraph WS* R_PAREN
   {
      final TextEffect effect;

      effect =
         WORLD.text_effects().get
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      $result =
         TextWithEffect.build
         (
            CONTEXT.get_origin_at
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
         value_list WS*
      R_PAREN WS+
      paragraph WS*
      R_PAREN
   {
      final TextEffect effect;

      effect =
         WORLD.text_effects().get
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      $result =
         TextWithEffect.build
         (
            CONTEXT.get_origin_at
            (
               ($ENABLE_TEXT_EFFECT_KW.getLine()),
               ($ENABLE_TEXT_EFFECT_KW.getCharPositionInLine())
            ),
            effect,
            ($value_list.result),
            ($paragraph.result)
         );
   }

   | NEWLINE_KW
   {
      $result =
         new Newline
         (
            CONTEXT.get_origin_at
            (
               ($NEWLINE_KW.getLine()),
               ($NEWLINE_KW.getCharPositionInLine())
            )
         );
   }

   | non_text_value
   {
      $result = ($non_text_value.result);
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

non_text_value
returns [Computation result]
:
   STRING_KW sentence WS* R_PAREN
   {
      $result = $sentence.result;
   }

   | IF_ELSE_KW cond=non_text_value WS+
      if_true=value WS+
      if_false=value WS*
   R_PAREN
   {
      $result =
         IfElseValue.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($cond.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | CONS_KW v0=value WS+ v1=value WS* R_PAREN
   {
      $result =
         new ConsComputation
         (
            CONTEXT.get_origin_at
            (
               ($CONS_KW.getLine()),
               ($CONS_KW.getCharPositionInLine())
            ),
            ($v0.result),
            ($v1.result)
         );
   }

   | CAR_KW non_text_value WS* R_PAREN
   {
      $result =
         CarCdr.build
         (
            CONTEXT.get_origin_at
            (
               ($CAR_KW.getLine()),
               ($CAR_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            true
         );
   }

   | CDR_KW non_text_value WS* R_PAREN
   {
      $result =
         CarCdr.build
         (
            CONTEXT.get_origin_at
            (
               ($CDR_KW.getLine()),
               ($CDR_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            false
         );
   }

   | ACCESS_KW index=non_text_value WS+ collection=non_text_value WS* R_PAREN
   {
      $result =
         Access.build
         (
            CONTEXT.get_origin_at
            (
               ($ACCESS_KW.getLine()),
               ($ACCESS_KW.getCharPositionInLine())
            ),
            ($collection.result),
            ($index.result)
         );
   }

   | ACCESS_POINTER_KW non_text_value WS+ value_reference WS* R_PAREN
   {
      $result =
         AccessPointer.build
         (
            CONTEXT.get_origin_at
            (
               ($ACCESS_POINTER_KW.getLine()),
               ($ACCESS_POINTER_KW.getCharPositionInLine())
            ),
            ($value_reference.result),
            ($non_text_value.result)
         );
   }

   | FIELD_ACCESS_KW WORD WS+ non_text_value WS* R_PAREN
   {
      $result =
         FieldAccess.build
         (
            CONTEXT.get_origin_at
            (
               ($FIELD_ACCESS_KW.getLine()),
               ($FIELD_ACCESS_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            ($WORD.text)
         );
   }

   | FOLDL_KW
         fun=non_text_value WS+
         init=value WS+
         inr=non_text_value WS*
      R_PAREN
   {
      $result =
         Fold.build
         (
            CONTEXT.get_origin_at
            (
               ($FOLDL_KW.getLine()),
               ($FOLDL_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($init.result),
            ($inr.result),
            true,
            new ArrayList()
         );
   }

   | FOLDL_KW
         fun=non_text_value WS+
         init=value WS+
         inr=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         Fold.build
         (
            CONTEXT.get_origin_at
            (
               ($FOLDL_KW.getLine()),
               ($FOLDL_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($init.result),
            ($inr.result),
            true,
            ($value_list.result)
         );
   }

   | FOLDR_KW
         fun=non_text_value WS+
         init=value WS+
         inr=non_text_value WS*
      R_PAREN
   {
      $result =
         Fold.build
         (
            CONTEXT.get_origin_at
            (
               ($FOLDR_KW.getLine()),
               ($FOLDR_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($init.result),
            ($inr.result),
            false,
            new ArrayList()
         );
   }

   | FOLDR_KW
         fun=non_text_value WS+
         init=value WS+
         inr=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         Fold.build
         (
            CONTEXT.get_origin_at
            (
               ($FOLDR_KW.getLine()),
               ($FOLDR_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($init.result),
            ($inr.result),
            false,
            ($value_list.result)
         );
   }

   | RANGE_KW
      vstart=non_text_value WS+
      vend=non_text_value WS+
      inc=non_text_value WS*
      R_PAREN
   {
      $result =
        Range.build
         (
            CONTEXT.get_origin_at
            (
               ($RANGE_KW.getLine()),
               ($RANGE_KW.getCharPositionInLine())
            ),
            ($vstart.result),
            ($vend.result),
            ($inc.result)
         );
   }

   | DEFAULT_KW type WS* R_PAREN
   {
      $result =
         new Default
         (
            CONTEXT.get_origin_at
            (
               ($DEFAULT_KW.getLine()),
               ($DEFAULT_KW.getCharPositionInLine())
            ),
            ($type.result)
         );
   }

   | COND_KW value_cond_list WS* R_PAREN
   {
      $result =
         CondValue.build
         (
            CONTEXT.get_origin_at
            (
               ($COND_KW.getLine()),
               ($COND_KW.getCharPositionInLine())
            ),
            ($value_cond_list.result)
         );
   }

   | SWITCH_KW
         target=value WS*
         value_switch_list WS*
         default_val=value WS*
      R_PAREN
   {
      $result =
         SwitchValue.build
         (
            CONTEXT.get_origin_at
            (
               ($SWITCH_KW.getLine()),
               ($SWITCH_KW.getCharPositionInLine())
            ),
            ($target.result),
            ($value_switch_list.result),
            ($default_val.result)
         );
   }

   | boolean_expression
   {
      $result = ($boolean_expression.result);
   }

   | LAMBDA_KW
         L_PAREN WS* variable_list WS* R_PAREN
         {
            final Map<String, Variable> variable_map;

            variable_map = new HashMap<String, Variable>();

            variable_map.putAll(($variable_list.result).as_map());

            LOCAL_VARIABLES.push(variable_map);
         }
         WS*
         value
         WS*
      R_PAREN
      {
         $result =
            LambdaExpression.build
            (
               CONTEXT.get_origin_at
               (
                  ($LAMBDA_KW.getLine()),
                  ($LAMBDA_KW.getCharPositionInLine())
               ),
               ($variable_list.result).get_entries(),
               ($value.result)
            );

         LOCAL_VARIABLES.pop();
      }

   | LET_KW
         L_PAREN WS* let_variable_list WS* R_PAREN
         WS*
         value
         WS*
      R_PAREN
      {
         final List<Cons<Variable, Computation>> let_list;

         let_list = ($let_variable_list.result);

         $result =
            new Let
            (
               CONTEXT.get_origin_at
               (
                  ($LET_KW.getLine()),
                  ($LET_KW.getCharPositionInLine())
               ),
               let_list,
               ($value.result)
            );

         for (final Cons<Variable, Computation> entry: let_list)
         {
            LOCAL_VARIABLES.peekFirst().remove(entry.get_car().get_name());
         }
      }

   | math_expression
   {
      $result = ($math_expression.result);
   }

   | REF_KW value_reference WS* R_PAREN
   {
      $result =
         new AddressOperator
         (
            CONTEXT.get_origin_at
            (
               ($REF_KW.getLine()),
               ($REF_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | CAST_KW WORD WS+ value WS* R_PAREN
   {
      final Origin target_type_origin;
      final Type target_type;

      target_type_origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      target_type = WORLD.types().get(target_type_origin, ($WORD.text));

      $result =
         Cast.build
         (
            CONTEXT.get_origin_at
            (
               ($CAST_KW.getLine()),
               ($CAST_KW.getCharPositionInLine())
            ),
            target_type,
            ($value.result),
            false
         );
   }

   | EXTRA_COMPUTATION_KW WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final ExtraComputation extra_computation;

      origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      extra_computation = WORLD.extra_computations().get(origin, ($WORD.text));

      $result =
         extra_computation.instantiate
         (
            WORLD,
            CONTEXT,
            origin,
            ($value_list.result)
         );
   }

   | EXTRA_COMPUTATION_KW WORD WS* R_PAREN
   {
      final Origin origin;
      final ExtraComputation extra_computation;

      origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      extra_computation = WORLD.extra_computations().get(origin, ($WORD.text));

      $result =
         extra_computation.instantiate
         (
            WORLD,
            CONTEXT,
            origin,
            new ArrayList<Computation>()
         );
   }

   | SEQUENCE_KW WORD WS* R_PAREN
   {
      final SequenceReference sr;
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($SEQUENCE_KW.getLine()),
            ($SEQUENCE_KW.getCharPositionInLine())
         );

      sr = new SequenceReference(origin, ($WORD.text));
      $result = sr;

      WORLD.add_sequence_variable(sr);
   }

   | EVAL_KW value_reference WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($EVAL_KW.getLine()),
            ($EVAL_KW.getCharPositionInLine())
         );

      $result =
         LambdaEvaluation.build
         (
            origin,
            ($value_reference.result),
            new ArrayList()
         );
   }

   | EVAL_KW value_reference WS+ value_list WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($EVAL_KW.getLine()),
            ($EVAL_KW.getCharPositionInLine())
         );

      $result =
         LambdaEvaluation.build
         (
            origin,
            ($value_reference.result),
            ($value_list.result)
         );
   }

   | ADD_KW vall=value_list WS+ coll=non_text_value WS* R_PAREN
   {
      $result = ($coll.result);

      for (final Computation value: ($vall.result))
      {
         $result =
            AddElementComputation.build
            (
               CONTEXT.get_origin_at
               (
                  ($ADD_KW.getLine()),
                  ($ADD_KW.getCharPositionInLine())
               ),
               value,
               $result
            );
      }
   }

   | ADD_AT_KW
         index=non_text_value WS+
         element=value WS+
         coll=non_text_value WS*
      R_PAREN
   {
      $result =
         AddElementAtComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($ADD_AT_KW.getLine()),
               ($ADD_AT_KW.getCharPositionInLine())
            ),
            ($index.result),
            ($element.result),
            ($coll.result)
         );
   }

   | ADD_ALL_KW
         sourcev=non_text_value WS+
         targetv=non_text_value WS*
      R_PAREN
   {
      $result =
         AddElementsOfComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($ADD_ALL_KW.getLine()),
               ($ADD_ALL_KW.getCharPositionInLine())
            ),
            ($sourcev.result),
            ($targetv.result)
         );
   }

   | REMOVE_ONE_KW val=value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         RemoveElementComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($REMOVE_ONE_KW.getLine()),
               ($REMOVE_ONE_KW.getCharPositionInLine())
            ),
            ($val.result),
            ($coll.result)
         );
   }

   | REMOVE_AT_KW val=value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         RemoveElementAtComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($REMOVE_AT_KW.getLine()),
               ($REMOVE_AT_KW.getCharPositionInLine())
            ),
            ($val.result),
            ($coll.result)
         );
   }

   | REMOVE_ALL_KW val=value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         RemoveAllOfElementComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($REMOVE_ALL_KW.getLine()),
               ($REMOVE_ALL_KW.getCharPositionInLine())
            ),
            ($val.result),
            ($coll.result)
         );
   }

   | REVERSE_KW non_text_value WS* R_PAREN
   {
      $result =
         ReverseListComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($REVERSE_KW.getLine()),
               ($REVERSE_KW.getCharPositionInLine())
            ),
            ($non_text_value.result)
         );
   }

   | PUSH_LEFT_KW val=value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         PushElementComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($PUSH_LEFT_KW.getLine()),
               ($PUSH_LEFT_KW.getCharPositionInLine())
            ),
            ($val.result),
            ($coll.result),
            true
         );
   }

   | PUSH_RIGHT_KW val=value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         PushElementComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($PUSH_RIGHT_KW.getLine()),
               ($PUSH_RIGHT_KW.getCharPositionInLine())
            ),
            ($val.result),
            ($coll.result),
            false
         );
   }

   | POP_LEFT_KW non_text_value WS* R_PAREN
   {
      $result =
         PopElementComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($POP_LEFT_KW.getLine()),
               ($POP_LEFT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            true
         );
   }

   | POP_RIGHT_KW non_text_value WS* R_PAREN
   {
      $result =
         PopElementComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($POP_RIGHT_KW.getLine()),
               ($POP_RIGHT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            false
         );
   }

   | MAP_KW fun=non_text_value WS+ inv=non_text_value WS* R_PAREN
   {
      $result =
         tonkadur.fate.v1.lang.computation.MapComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($MAP_KW.getLine()),
               ($MAP_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv.result),
            new ArrayList()
         );
   }

   | MAP_KW fun=non_text_value WS+ inv=non_text_value WS+ value_list WS* R_PAREN
   {
      $result =
         tonkadur.fate.v1.lang.computation.MapComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($MAP_KW.getLine()),
               ($MAP_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv.result),
            ($value_list.result)
         );
   }

   | INDEXED_MAP_KW fun=non_text_value WS+ inv=non_text_value WS* R_PAREN
   {
      $result =
         IndexedMapComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_MAP_KW.getLine()),
               ($INDEXED_MAP_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv.result),
            new ArrayList()
         );
   }

   | INDEXED_MAP_KW
         fun=non_text_value WS+
         inv=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMapComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_MAP_KW.getLine()),
               ($INDEXED_MAP_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv.result),
            ($value_list.result)
         );
   }

   | INDEXED_MERGE_TO_LIST_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_MERGE_TO_LIST_KW.getLine()),
               ($INDEXED_MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            false,
            new ArrayList()
         );
   }

   | INDEXED_MERGE_TO_LIST_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_MERGE_TO_LIST_KW.getLine()),
               ($INDEXED_MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            false,
            ($value_list.result)
         );
   }

   | SAFE_INDEXED_MERGE_TO_LIST_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_INDEXED_MERGE_TO_LIST_KW.getLine()),
               ($SAFE_INDEXED_MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            false,
            new ArrayList()
         );
   }

   | SAFE_INDEXED_MERGE_TO_LIST_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_INDEXED_MERGE_TO_LIST_KW.getLine()),
               ($SAFE_INDEXED_MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            false,
            ($value_list.result)
         );
   }

   | MERGE_TO_LIST_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($MERGE_TO_LIST_KW.getLine()),
               ($MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            false,
            new ArrayList()
         );
   }

   | MERGE_TO_LIST_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($MERGE_TO_LIST_KW.getLine()),
               ($MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            false,
            ($value_list.result)
         );
   }

   | SAFE_MERGE_TO_LIST_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_MERGE_TO_LIST_KW.getLine()),
               ($SAFE_MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            false,
            new ArrayList()
         );
   }

   | SAFE_MERGE_TO_LIST_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_MERGE_TO_LIST_KW.getLine()),
               ($SAFE_MERGE_TO_LIST_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            false,
            ($value_list.result)
         );
   }

   | INDEXED_MERGE_TO_SET_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_MERGE_TO_SET_KW.getLine()),
               ($INDEXED_MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            true,
            new ArrayList()
         );
   }

   | INDEXED_MERGE_TO_SET_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_MERGE_TO_SET_KW.getLine()),
               ($INDEXED_MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            true,
            ($value_list.result)
         );
   }

   | SAFE_INDEXED_MERGE_TO_SET_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_INDEXED_MERGE_TO_SET_KW.getLine()),
               ($SAFE_INDEXED_MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            true,
            new ArrayList()
         );
   }

   | SAFE_INDEXED_MERGE_TO_SET_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedMergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_INDEXED_MERGE_TO_SET_KW.getLine()),
               ($SAFE_INDEXED_MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            true,
            ($value_list.result)
         );
   }

   | MERGE_TO_SET_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($MERGE_TO_SET_KW.getLine()),
               ($MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            true,
            new ArrayList()
         );
   }

   | MERGE_TO_SET_KW
         fun=non_text_value WS+
         inv0=non_text_value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($MERGE_TO_SET_KW.getLine()),
               ($MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            null,
            ($inv1.result),
            null,
            true,
            ($value_list.result)
         );
   }

   | SAFE_MERGE_TO_SET_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_MERGE_TO_SET_KW.getLine()),
               ($SAFE_MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            true,
            new ArrayList()
         );
   }

   | SAFE_MERGE_TO_SET_KW
         fun=non_text_value WS+
         def0=value WS+
         inv0=non_text_value WS+
         def1=value WS+
         inv1=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         MergeComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SAFE_MERGE_TO_SET_KW.getLine()),
               ($SAFE_MERGE_TO_SET_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($inv0.result),
            ($def0.result),
            ($inv1.result),
            ($def1.result),
            true,
            ($value_list.result)
         );
   }

   | SUB_LIST_KW
      vstart=non_text_value WS+
      vend=non_text_value WS+
      inv=non_text_value WS*
      R_PAREN
   {
      $result =
         SubListComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SUB_LIST_KW.getLine()),
               ($SUB_LIST_KW.getCharPositionInLine())
            ),
            ($vstart.result),
            ($vend.result),
            ($inv.result)
         );
   }

   | FILTER_KW fun=non_text_value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         FilterComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($FILTER_KW.getLine()),
               ($FILTER_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            new ArrayList()
         );
   }

   | FILTER_KW
         fun=non_text_value WS+
         coll=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         FilterComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($FILTER_KW.getLine()),
               ($FILTER_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            ($value_list.result)
         );
   }

   | INDEXED_FILTER_KW fun=non_text_value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         IndexedFilterComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_FILTER_KW.getLine()),
               ($INDEXED_FILTER_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            new ArrayList()
         );
   }

   | INDEXED_FILTER_KW
         fun=non_text_value WS+
         coll=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedFilterComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_FILTER_KW.getLine()),
               ($INDEXED_FILTER_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            ($value_list.result)
         );
   }

   | PARTITION_KW fun=non_text_value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
        PartitionComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($PARTITION_KW.getLine()),
               ($PARTITION_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            new ArrayList()
         );
   }

   | PARTITION_KW
         fun=non_text_value WS+
         coll=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
        PartitionComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($PARTITION_KW.getLine()),
               ($PARTITION_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            ($value_list.result)
         );
   }

   | INDEXED_PARTITION_KW fun=non_text_value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         IndexedPartitionComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_PARTITION_KW.getLine()),
               ($INDEXED_PARTITION_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            new ArrayList()
         );
   }

   | INDEXED_PARTITION_KW
         fun=non_text_value WS+
         coll=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         IndexedPartitionComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($INDEXED_PARTITION_KW.getLine()),
               ($INDEXED_PARTITION_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            ($value_list.result)
         );
   }

   | SORT_KW fun=non_text_value WS+ coll=non_text_value WS* R_PAREN
   {
      $result =
         SortComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SORT_KW.getLine()),
               ($SORT_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            new ArrayList()
         );
   }

   | SORT_KW
         fun=non_text_value WS+
         coll=non_text_value WS+
         value_list WS*
      R_PAREN
   {
      $result =
         SortComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SORT_KW.getLine()),
               ($SORT_KW.getCharPositionInLine())
            ),
            ($fun.result),
            ($coll.result),
            ($value_list.result)
         );
   }


   | SHUFFLE_KW non_text_value WS* R_PAREN
   {
      $result =
        ShuffleComputation.build
         (
            CONTEXT.get_origin_at
            (
               ($SHUFFLE_KW.getLine()),
               ($SHUFFLE_KW.getCharPositionInLine())
            ),
            ($non_text_value.result)
         );
   }

   | SET_FIELDS_KW non_text_value WS* field_value_list WS* R_PAREN
   {
      /*
       * A bit of a lazy solution: build field references, then extract the data
       */
      final List<Cons<String, Computation>> assignments;

      assignments = new ArrayList<Cons<String, Computation>>();

      for
      (
         final Cons<Origin, Cons<String, Computation>> entry:
            ($field_value_list.result)
      )
      {
         final FieldReference fr;
         final Computation cp;

         fr =
            FieldReference.build
            (
               entry.get_car(),
               ($non_text_value.result),
               entry.get_cdr().get_car()
            );

         cp = entry.get_cdr().get_cdr();

         RecurrentChecks.assert_can_be_used_as(cp, fr.get_type());

         assignments.add(new Cons(fr.get_field_name(), cp));
      }

      $result =
         new SetFieldsComputation
         (
            CONTEXT.get_origin_at
            (
               ($SET_FIELDS_KW.getLine()),
               ($SET_FIELDS_KW.getCharPositionInLine())
            ),
            ($non_text_value.result),
            assignments
         );
   }

   | WORD
   {
      $result = null;

      $result =
         Constant.build_if_non_string
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );

      if ($result == null)
      {
         $result =
            VariableFromWord.generate
            (
               WORLD,
               LOCAL_VARIABLES,
               CONTEXT.get_origin_at
               (
                  ($WORD.getLine()),
                  ($WORD.getCharPositionInLine())
               ),
               ($WORD.text)
            );
      }
   }

   | value_reference
   {
      $result = ($value_reference.result);
   }

;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

value_reference
returns [Reference result]
:
   AT_KW non_text_value WS* R_PAREN
   {
      $result =
         AtReference.build
         (
            CONTEXT.get_origin_at
            (
               ($AT_KW.getLine()),
               ($AT_KW.getCharPositionInLine())
            ),
            ($non_text_value.result)
         );
   }

   | FIELD_KW WORD WS+ value_reference WS* R_PAREN
   {
      $result =
         FieldReference.build
         (
            CONTEXT.get_origin_at
            (
               ($FIELD_KW.getLine()),
               ($FIELD_KW.getCharPositionInLine())
            ),
            ($value_reference.result),
            ($WORD.text)
         );
   }


   | (WORD | (VARIABLE_KW WORD WS* R_PAREN))
   {
      $result =
         VariableFromWord.generate
         (
            WORLD,
            LOCAL_VARIABLES,
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

value_cond_list
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
               L_PAREN WS* c=non_text_value WS+
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
                     WORLD,
                     LOCAL_VARIABLES,
                     CONTEXT.get_origin_at
                     (
                        ($something_else.getLine()),
                        ($something_else.getCharPositionInLine())
                     ),
                     ($something_else.text).substring(1).trim()
                  );
            }
         )
      )
      v=value WS* R_PAREN WS*
      {
         $result.add(new Cons(condition, ($v.result)));
      }
   )+
   {
   }
;
catch [final Throwable e]
{
   if ((e.getMessage() == null) || !e.getMessage().startsWith("Require"))
   {
      throw new ParseCancellationException(CONTEXT.toString() + ((e.getMessage() == null) ? "" : e.getMessage()), e);
   }
   else
   {
      throw new ParseCancellationException(e);
   }
}

value_switch_list
returns [List<Cons<Computation, Computation>> result]
@init
{
   $result = new ArrayList<Cons<Computation, Computation>>();
}
:
   (
      L_PAREN WS* c=value WS+ v=value WS* R_PAREN WS*
      {
         $result.add(new Cons(($c.result), ($v.result)));
      }
   )+
   {
   }
;

value_list
returns [List<Computation> result]
@init
{
   $result = new ArrayList<Computation>();
}
:
   (
      value
      {
         ($result).add(($value.result));
      }
   )*
   (WS+
      value
      {
         ($result).add(($value.result));
      }
   )*
   {
   }
;

non_text_value_list
returns [List<Computation> result]
@init
{
   $result = new ArrayList<Computation>();
}
:
   (
      non_text_value
      {
         ($result).add(($non_text_value.result));
      }
   )*
   (WS+
      non_text_value
      {
         ($result).add(($non_text_value.result));
      }
   )*
   {
   }
;
