parser grammar FateParser;

options
{
   tokenVocab = FateLexer;
}

@header
{
   package tonkadur.fate.v1.parser;

   import java.util.Arrays;
   import java.util.Map;
   import java.util.HashMap;

   import tonkadur.Files;

   import tonkadur.error.ErrorManager;

   import tonkadur.functional.Cons;

   import tonkadur.parser.Context;
   import tonkadur.parser.Location;
   import tonkadur.parser.Origin;

   import tonkadur.fate.v1.Utils;

   import tonkadur.fate.v1.error.IllegalReferenceNameException;
   import tonkadur.fate.v1.error.NotInAMacroException;
   import tonkadur.fate.v1.error.UnknownExtensionContentException;
   import tonkadur.fate.v1.error.UnknownParameterException;
   import tonkadur.fate.v1.error.UnknownVariableScopeException;

   import tonkadur.fate.v1.lang.*;
   import tonkadur.fate.v1.lang.instruction.*;
   import tonkadur.fate.v1.lang.meta.*;
   import tonkadur.fate.v1.lang.type.*;
   import tonkadur.fate.v1.lang.valued_node.*;
}

@members
{
   Context CONTEXT;
   World WORLD;
   TypedEntryList PARAMETERS;
}

/******************************************************************************/
/******************************************************************************/
/******************************************************************************/
fate_file [Context context, World world]
   @init
   {
      CONTEXT = context;
      WORLD = world;
      PARAMETERS = null;
   }
   :
   WS* FATE_VERSION_KW WS+ WORD WS* R_PAREN WS*
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
returns [List<InstructionNode> result]
@init
{
   $result = new ArrayList<InstructionNode>();
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
      WS+
      new_reference_name
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
            ($new_reference_name.result)
         );

      WORLD.sequences().add(new_sequence);
   }

   | DECLARE_VARIABLE_KW
      WS+
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
            VariableScope.LOCAL,
            ($type.result),
            ($name.result)
         );

      WORLD.variables().add(new_variable);
   }

   | DECLARE_VARIABLE_KW
      WS+
      scope=WORD
      WS+
      type
      WS+
      name=new_reference_name
      WS*
   R_PAREN
   {
      final Origin start_origin, scope_origin, type_origin;
      final Variable new_variable;
      VariableScope variable_scope;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_VARIABLE_KW.getLine()),
            ($DECLARE_VARIABLE_KW.getCharPositionInLine())
         );

      scope_origin =
         CONTEXT.get_origin_at
         (
            ($scope.getLine()),
            ($scope.getCharPositionInLine())
         );

      variable_scope = VariableScope.value_of(($scope.text));

      if (variable_scope == null)
      {
         ErrorManager.handle
         (
            new UnknownVariableScopeException(scope_origin, ($scope.text))
         );

         variable_scope = VariableScope.ANY;
      }

      new_variable =
         new Variable
         (
            start_origin,
            variable_scope,
            ($type.result),
            ($name.result)
         );

      WORLD.variables().add(new_variable);
   }

   | DECLARE_TEXT_EFFECT_KW
      WS+
      params=type_list
      WS*
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
            ($type_list.result),
            ($new_reference_name.result)
         );

      WORLD.text_effects().add(new_text_effect);
   }

   | REQUIRE_EXTENSION_KW WS+ WORD WS* R_PAREN
   {
      WORLD.add_required_extension(($WORD.text));

      /* TODO: error report if extension not explicitly enabled. */
   }

   | DECLARE_ALIAS_TYPE_KW WS+ parent=type WS+ new_reference_name WS* R_PAREN
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
         Type.build
         (
            start_origin,
            ($parent.result),
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_SET_TYPE_KW WS+ parent=type WS+ new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final Type new_type;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_SET_TYPE_KW.getLine()),
            ($DECLARE_SET_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         CollectionType.build
         (
            start_origin,
            ($parent.result),
            true,
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_LIST_TYPE_KW WS+ parent=type WS+ new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final Type new_type;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_LIST_TYPE_KW.getLine()),
            ($DECLARE_LIST_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         CollectionType.build
         (
            start_origin,
            ($parent.result),
            false,
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_REF_TYPE_KW WS+ parent=type WS+ new_reference_name WS* R_PAREN
   {
      final Origin start_origin;
      final Type new_type;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_REF_TYPE_KW.getLine()),
            ($DECLARE_REF_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         new RefType
         (
            start_origin,
            ($parent.result),
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_DICT_TYPE_KW
      WS+
      new_reference_name
      WS*
      typed_entry_list
      WS*
   R_PAREN
   {
      final Origin start_origin;
      final Type new_type;
      final Map<String, Type> field_types;

      field_types = new HashMap<String, Type>();

      for
      (
         final TypedEntryList.TypedEntry te:
            ($typed_entry_list.result).get_entries()
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


   | DECLARE_EVENT_TYPE_KW WS+ new_reference_name WS+ type_list WS* R_PAREN
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

   | REQUIRE_KW WS+ WORD WS* R_PAREN
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

         Utils.add_file_content(filename, CONTEXT, WORLD);

         CONTEXT.pop();
      }
   }

   | INCLUDE_KW WS+ WORD WS* R_PAREN
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

      Utils.add_file_content(filename, CONTEXT, WORLD);

      CONTEXT.pop();
   }

   | DEFINE_MACRO_KW
         WS+
         new_reference_name
         WS*
         (
            L_PAREN WS+ typed_entry_list WS* R_PAREN
            {
               PARAMETERS=($typed_entry_list.result);
            }
         )
         WS*
         general_fate_sequence
         WS*
      R_PAREN
   {
      final Origin origin;
      final Macro new_macro;

      PARAMETERS = null;

      origin =
         CONTEXT.get_origin_at
         (
            ($DEFINE_MACRO_KW.getLine()),
            ($DEFINE_MACRO_KW.getCharPositionInLine())
         );

      new_macro =
         new Macro
         (
            origin,
            new InstructionList
            (
               origin,
               ($general_fate_sequence.result)
            ),
            ($typed_entry_list.result),
            ($new_reference_name.result)
         );

      WORLD.macros().add(new_macro);
   }

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
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

general_fate_instr
returns [InstructionNode result]
:
   L_PAREN WS+ general_fate_sequence WS* R_PAREN
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

   | ADD_KW WS+ value WS+ value_reference WS* R_PAREN
   {
      $result =
         AddElement.build
         (
            CONTEXT.get_origin_at
            (
               ($ADD_KW.getLine()),
               ($ADD_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | REMOVE_ONE_KW WS+ value WS+ value_reference WS* R_PAREN
   {
      $result =
         RemoveElement.build
         (
            CONTEXT.get_origin_at
            (
               ($REMOVE_ONE_KW.getLine()),
               ($REMOVE_ONE_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | REMOVE_ALL_KW WS+ value WS+ value_reference WS* R_PAREN
   {
      $result =
         RemoveAllOfElement.build
         (
            CONTEXT.get_origin_at
            (
               ($REMOVE_ALL_KW.getLine()),
               ($REMOVE_ALL_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($value_reference.result)
         );
   }

   | CLEAR_KW WS+ value_reference WS* R_PAREN
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

   | SET_KW WS+ value WS+ value_reference WS* R_PAREN
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

   | SET_FIELDS_KW WS+ value_reference WS* field_value_list WS* R_PAREN
   {
      final Origin origin;
      final List<InstructionNode> operations;

      origin =
         CONTEXT.get_origin_at
         (
            ($SET_FIELDS_KW.getLine()),
            ($SET_FIELDS_KW.getCharPositionInLine())
         );

      operations = new ArrayList<InstructionNode>();

      for
      (
         final Cons<Origin, Cons<String, ValueNode>> entry:
            ($field_value_list.result)
      )
      {
         operations.add
         (
            SetValue.build
            (
               entry.get_car(),
               entry.get_cdr().get_cdr(),
               FieldReference.build
               (
                  entry.get_car(),
                  ($value_reference.result),
                  entry.get_cdr().get_car()
               )
            )
         );
      }

      $result = new InstructionList(origin, operations);
   }

   | EVENT_KW WS+ WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final Event event;

      origin =
         CONTEXT.get_origin_at
         (
            ($EVENT_KW.getLine()),
            ($EVENT_KW.getCharPositionInLine())
         );

      event = WORLD.events().get(origin, ($WORD.text));

      $result =
         EventCall.build
         (
            origin,
            event,
            ($value_list.result)
         );
   }

   | MACRO_KW WS+ WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final Macro macro;

      origin =
         CONTEXT.get_origin_at
         (
            ($MACRO_KW.getLine()),
            ($MACRO_KW.getCharPositionInLine())
         );

      macro = WORLD.macros().get(origin, ($WORD.text));

      $result =
         MacroCall.build
         (
            origin,
            macro,
            ($value_list.result)
         );
   }

   | SEQUENCE_KW WS+ WORD WS* R_PAREN
   {
      final Origin origin;
      final String sequence_name;

      origin =
         CONTEXT.get_origin_at
         (
            ($SEQUENCE_KW.getLine()),
            ($SEQUENCE_KW.getCharPositionInLine())
         );

      sequence_name = ($WORD.text);

      WORLD.add_sequence_call(origin, sequence_name);

      $result = new SequenceCall(origin, sequence_name);
   }

   | ASSERT_KW WS+ value WS* R_PAREN
   {
      $result =
         Assert.build
         (
            CONTEXT.get_origin_at
            (
               ($ASSERT_KW.getLine()),
               ($ASSERT_KW.getCharPositionInLine())
            ),
            ($value.result)
         );
   }

   | IF_KW WS+ value WS* general_fate_instr WS* R_PAREN
   {
      $result =
         IfInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($general_fate_instr.result)
         );
   }

   | IF_ELSE_KW
         WS+ value
         WS+ if_true=general_fate_instr
         WS+ if_false=general_fate_instr
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
            ($value.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | COND_KW WS+ instr_cond_list WS* R_PAREN
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

   | PLAYER_CHOICE_KW WS+ player_choice_list WS* R_PAREN
   {
      $result =
         new PlayerChoiceList
         (
            CONTEXT.get_origin_at
            (
               ($PLAYER_CHOICE_KW.getLine()),
               ($PLAYER_CHOICE_KW.getCharPositionInLine())
            ),
            ($player_choice_list.result)
         );
   }

   | EXTENSION_INSTRUCTION_KW WORD WS+ general_fate_sequence WS* R_PAREN
   {
      final Origin origin;
      final ExtensionInstruction instr;

      origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      instr = WORLD.extension_instructions().get(($WORD.text));

      if (instr == null)
      {
         ErrorManager.handle
         (
            new UnknownExtensionContentException(origin, ($WORD.text))
         );
      }
      else
      {
         $result =
            instr.build
            (
               WORLD,
               CONTEXT,
               origin,
               ($general_fate_sequence.result)
            );
      }
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
   throw new ParseCancellationException(e);
}

instr_cond_list
returns [List<Cons<ValueNode, InstructionNode>> result]
@init
{
   $result = new ArrayList<Cons<ValueNode, InstructionNode>>();
}
:
   (
      L_PAREN WS* value WS+ general_fate_instr WS* R_PAREN
      {
         $result.add(new Cons(($value.result), ($general_fate_instr.result)));
      }
      WS*
   )+
   {
   }
;

player_choice_list
returns [List<InstructionNode> result]
@init
{
   $result = new ArrayList<InstructionNode>();
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
returns [InstructionNode result]
:
   start_p=L_PAREN WS*
      L_PAREN WS* paragraph WS* R_PAREN WS+
      general_fate_sequence WS*
   R_PAREN
   {
      $result =
         new PlayerChoice
         (
            CONTEXT.get_origin_at
            (
               ($start_p.getLine()),
               ($start_p.getCharPositionInLine())
            ),
            ($paragraph.result),
            ($general_fate_sequence.result)
         );
   }

   | IF_KW WS+ value WS+ player_choice WS* R_PAREN
   {
      $result =
         IfInstruction.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_KW.getLine()),
               ($IF_KW.getCharPositionInLine())
            ),
            ($value.result),
            ($player_choice.result)
         );
   }

   | IF_ELSE_KW WS+
      value WS+
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
            ($value.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | COND_KW WS+ player_choice_cond_list WS* R_PAREN
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
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

player_choice_cond_list
returns [List<Cons<ValueNode, InstructionNode>> result]
@init
{
   $result = new ArrayList<Cons<ValueNode, InstructionNode>>();
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
   throw new ParseCancellationException(e);
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
            content.add(new Space(($next_a.result.get_origin())));
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

text
returns [TextNode result]:
   sentence
   {
      $result = ($sentence.result);
   }

   | ENABLE_TEXT_EFFECT_KW WS+ WORD WS+ paragraph WS* R_PAREN
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
            new ArrayList<ValueNode>(),
            ($paragraph.result)
         );
   }

   | ENABLE_TEXT_EFFECT_KW WS+
      L_PAREN
         WORD WS+
         value_list
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
   throw new ParseCancellationException(e);
}

sentence
returns [TextNode result]
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
         new Sentence
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
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
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

typed_entry_list
returns [TypedEntryList result]
@init
{
   $result = new TypedEntryList();
}
:
   (
      WS*
      L_PAREN WS* type WS+ new_reference_name WS* R_PAREN
      {
         $result.add
         (
            CONTEXT.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($type.result),
            ($new_reference_name.result)
         );
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

field_value_list
returns [List<Cons<Origin, Cons<String, ValueNode>>> result]
@init
{
   $result = new ArrayList<Cons<Origin, Cons<String, ValueNode>>>();
}
:
   (
      WS*
      L_PAREN WS* WORD WS+ value WS* R_PAREN
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
                  ($WORD.text),
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
   throw new ParseCancellationException(e);
}

/******************************************************************************/
/**** VALUES ******************************************************************/
/******************************************************************************/
boolean_expression
returns [ValueNode result]:
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

   | AND_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | OR_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | ONE_IN_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | NOT_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | IMPLIES_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | LOWER_THAN_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | LOWER_EQUAL_THAN_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | EQUALS_KW WS+ value_list WS* R_PAREN
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

   | GREATER_EQUAL_THAN_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | GREATER_THAN_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | IS_MEMBER_KW WS+ value WS+ value_reference WS* R_PAREN
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
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

math_expression
returns [ValueNode result]:
   PLUS_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | MINUS_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | TIMES_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | DIVIDE_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | POWER_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | RANDOM_KW WS+ value_list WS* R_PAREN
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
            ($value_list.result)
         );
   }

   | COUNT_KW WS+ value WS+ value_reference WS* R_PAREN
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
            ($value_reference.result)
         );
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

value
returns [ValueNode result]
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

   | L_PAREN WS+ paragraph WS* R_PAREN
   {
      $result = ($paragraph.result);
   }

   | ENABLE_TEXT_EFFECT_KW WS+ WORD WS+ paragraph WS* R_PAREN
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
            new ArrayList<ValueNode>(),
            ($paragraph.result)
         );
   }

   | ENABLE_TEXT_EFFECT_KW WS+
      L_PAREN
         WORD WS+
         value_list
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
   throw new ParseCancellationException(e);
}

non_text_value
returns [ValueNode result]
:
   IF_ELSE_KW WS+ cond=value WS+ if_true=value WS+ if_false=value WS* R_PAREN
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

   | COND_KW WS+ value_cond_list WS* R_PAREN
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

   | boolean_expression
   {
      $result = ($boolean_expression.result);
   }

   | math_expression
   {
      $result = ($math_expression.result);
   }

   | REF_KW WS+ value_reference WS* R_PAREN
   {
      $result =
         new RefOperator
         (
            CONTEXT.get_origin_at
            (
               ($REF_KW.getLine()),
               ($REF_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | CAST_KW WS+ WORD WS+ value WS* R_PAREN
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

   | EXTENSION_VALUE_KW WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final ExtensionValueNode value;

      origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      value = WORLD.extension_value_nodes().get(($WORD.text));

      if (value == null)
      {
         ErrorManager.handle
         (
            new UnknownExtensionContentException(origin, ($WORD.text))
         );
      }
      else
      {
         $result =
            value.build
            (
               WORLD,
               CONTEXT,
               origin,
               ($value_list.result)
            );
      }
   }

   | MACRO_KW WS+ WORD WS+ value_list WS* R_PAREN
   {
      final Origin origin;
      final Macro macro;

      origin =
         CONTEXT.get_origin_at
         (
            ($MACRO_KW.getLine()),
            ($MACRO_KW.getCharPositionInLine())
         );

      macro = WORLD.macros().get(origin, ($WORD.text));

      $result =
         MacroValueCall.build
         (
            origin,
            macro,
            ($value_list.result)
         );
   }

   | value_reference
   {
      $result = ($value_reference.result);
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

value_reference
returns [Reference result]
:
   AT_KW WS+ value_reference WS* R_PAREN
   {
      $result =
         AtReference.build
         (
            CONTEXT.get_origin_at
            (
               ($AT_KW.getLine()),
               ($AT_KW.getCharPositionInLine())
            ),
            ($value_reference.result)
         );
   }

   | FIELD_KW WS+ value_reference WORD R_PAREN
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

   | VARIABLE_KW WS+ WORD WS* R_PAREN
   {
      final Origin target_var_origin;
      final Variable target_var;
      final String[] subrefs;

      subrefs = ($WORD.text).split("\\.");

      target_var_origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      target_var = WORLD.variables().get(target_var_origin, subrefs[0]);

      $result =
         new VariableReference
         (
            CONTEXT.get_origin_at
            (
               ($VARIABLE_KW.getLine()),
               ($VARIABLE_KW.getCharPositionInLine())
            ),
            target_var
         );

      if (subrefs.length > 1)
      {
         final List<String> subrefs_list;

         subrefs_list = new ArrayList(Arrays.asList(subrefs));

         subrefs_list.remove(0);

         $result =
            FieldReference.build
            (
               target_var_origin,
               ($result),
               subrefs_list
            );
      }
   }

   | PARAMETER_KW WS+ WORD WS* R_PAREN
   {
      final Origin origin;

      origin =
         CONTEXT.get_origin_at
         (
            ($PARAMETER_KW.getLine()),
            ($PARAMETER_KW.getCharPositionInLine())
         );

      if (PARAMETERS == null)
      {
         ErrorManager.handle
         (
            new NotInAMacroException(origin)
         );
      }
      else
      {
         final TypedEntryList.TypedEntry param;
         final String[] subrefs;

         subrefs = ($WORD.text).split("\\.");

         param = PARAMETERS.as_map().get(subrefs[0]);

         if (param == null)
         {
            ErrorManager.handle
            (
               new UnknownParameterException(origin, subrefs[0], PARAMETERS)
            );

            $result = new ParameterReference(origin, Type.ANY, ($WORD.text));
         }
         else
         {
            $result =
               new ParameterReference
               (
                  origin,
                  param.get_type(),
                  ($WORD.text)
               );

            if (subrefs.length > 1)
            {
               final List<String> subrefs_list;

               subrefs_list = new ArrayList(Arrays.asList(subrefs));

               subrefs_list.remove(0);

               $result =
                  FieldReference.build
                  (
                     origin,
                     ($result),
                     subrefs_list
                  );
            }
         }
      }
   }

   | WORD
   {
      final Origin target_var_origin;
      final Variable target_var;
      final String[] subrefs;

      subrefs = ($WORD.text).split("\\.");

      target_var_origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      target_var = WORLD.variables().get(target_var_origin, subrefs[0]);

      $result =
         new VariableReference
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            target_var
         );

      if (subrefs.length > 1)
      {
         final List<String> subrefs_list;

         subrefs_list = new ArrayList(Arrays.asList(subrefs));

         subrefs_list.remove(0);

         $result =
            FieldReference.build
            (
               target_var_origin,
               ($result),
               subrefs_list
            );
      }
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

value_cond_list
returns [List<Cons<ValueNode, ValueNode>> result]
@init
{
   $result = new ArrayList<Cons<ValueNode, ValueNode>>();
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
returns [List<ValueNode> result]
@init
{
   $result = new ArrayList<ValueNode>();
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
